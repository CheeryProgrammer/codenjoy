/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
﻿using System;
using System.Xml;
using System.Threading.Tasks;
using SharpNeat.Decoders;

namespace neat_net_client
{
    public class XmlUtils
    {
        /// <summary>
        /// Parse the inner text of element with the given name as an integer. If element is missing or parsing fails then
        /// throws an ArgumentException.
        /// </summary>
        public static int GetValueAsInt(XmlElement xmlParent, string elemName)
        {
            int? val = TryGetValueAsInt(xmlParent, elemName);
            if (null == val)
            {
                throw new ArgumentException($"Missing [{elemName}] configuration setting.");
            }
            return val.Value;
        }

        /// <summary>
        /// Parse the inner text of element with the given name as an integer. If element is missing or parsing fails then
        /// returns null.
        /// </summary>
        public static int? TryGetValueAsInt(XmlElement xmlParent, string elemName)
        {
            XmlElement xmlElem = xmlParent.SelectSingleNode(elemName) as XmlElement;
            if (null == xmlElem)
            {
                return null;
            }

            string valStr = xmlElem.InnerText;
            if (string.IsNullOrEmpty(valStr))
            {
                return null;
            }

            int result;
            if (int.TryParse(valStr, out result))
            {
                return result;
            }
            return null;
        }

        /// <summary>
        /// Parse the inner text of element with the given name as a double. If element is missing or parsing fails then
        /// throws an ArgumentException.
        /// </summary>
        public static double GetValueAsDouble(XmlElement xmlParent, string elemName)
        {
            double? val = TryGetValueAsDouble(xmlParent, elemName);
            if (null == val)
            {
                throw new ArgumentException($"Missing [{elemName}] configuration setting.");
            }
            return val.Value;
        }

        /// <summary>
        /// Parse the inner text of element with the given name as a double. If element is missing or parsing fails then
        /// returns null.
        /// </summary>
        public static double? TryGetValueAsDouble(XmlElement xmlParent, string elemName)
        {
            XmlElement xmlElem = xmlParent.SelectSingleNode(elemName) as XmlElement;
            if (null == xmlElem)
            {
                return null;
            }

            string valStr = xmlElem.InnerText;
            if (string.IsNullOrEmpty(valStr))
            {
                return null;
            }

            double result;
            if (double.TryParse(valStr, out result))
            {
                return result;
            }
            return null;
        }

        /// <summary>
        /// Parse the inner text of element with the given name as a boolean. If element is missing or parsing fails then
        /// throws an ArgumentException.
        /// </summary>
        public static bool GetValueAsBool(XmlElement xmlParent, string elemName)
        {
            bool? val = TryGetValueAsBool(xmlParent, elemName);
            if (null == val)
            {
                throw new ArgumentException($"Missing [{elemName}] configuration setting.");
            }
            return val.Value;
        }

        /// <summary>
        /// Parse the inner text of element with the given name as a boolean. If element is missing or parsing fails then
        /// returns null.
        /// </summary>
        public static bool? TryGetValueAsBool(XmlElement xmlParent, string elemName)
        {
            XmlElement xmlElem = xmlParent.SelectSingleNode(elemName) as XmlElement;
            if (null == xmlElem)
            {
                return null;
            }

            string valStr = xmlElem.InnerText;
            if (string.IsNullOrEmpty(valStr))
            {
                return null;
            }

            bool result;
            if (bool.TryParse(valStr, out result))
            {
                return result;
            }
            return null;
        }

        /// <summary>
        /// Read the inner text of element with the given name. If element is missing then throws an ArgumentException.
        /// </summary>
        public static string GetValueAsString(XmlElement xmlParent, string elemName)
        {
            string val = TryGetValueAsString(xmlParent, elemName);
            if (null == val)
            {
                throw new ArgumentException($"Missing [{elemName}] configuration setting.");
            }
            return val;
        }

        /// <summary>
        /// Read the inner text of element with the given name. If element is missing then returns null.
        /// </summary>
        public static string TryGetValueAsString(XmlElement xmlParent, string elemName)
        {
            XmlElement xmlElem = xmlParent.SelectSingleNode(elemName) as XmlElement;
            if (null == xmlElem)
            {
                return null;
            }

            string valStr = xmlElem.InnerText;
            if (string.IsNullOrEmpty(valStr))
            {
                return null;
            }
            return valStr;
        }

        public static ParallelOptions ReadParallelOptions(XmlElement xmlConfig)
        {
            // Get parallel options.
            ParallelOptions parallelOptions;
            int? maxDegreeOfParallelism = XmlUtils.TryGetValueAsInt(xmlConfig, "MaxDegreeOfParallelism");
            if (null != maxDegreeOfParallelism)
            {
                parallelOptions = new ParallelOptions { MaxDegreeOfParallelism = maxDegreeOfParallelism.Value };
            }
            else
            {
                parallelOptions = new ParallelOptions();
            }
            return parallelOptions;
        }

        public static NetworkActivationScheme CreateActivationScheme(XmlElement xmlConfig, string activationElemName)
        {
            // Get root activation element.
            XmlNodeList nodeList = xmlConfig.GetElementsByTagName(activationElemName, "");
            if (nodeList.Count != 1)
            {
                throw new ArgumentException("Missing or invalid activation XML config setting.");
            }

            XmlElement xmlActivation = nodeList[0] as XmlElement;
            string schemeStr = XmlUtils.TryGetValueAsString(xmlActivation, "Scheme");
            switch (schemeStr)
            {
                case "Acyclic":
                    return NetworkActivationScheme.CreateAcyclicScheme();
                case "CyclicFixedIters":
                    int iters = XmlUtils.GetValueAsInt(xmlActivation, "Iters");
                    return NetworkActivationScheme.CreateCyclicFixedTimestepsScheme(iters);
                case "CyclicRelax":
                    double deltaThreshold = XmlUtils.GetValueAsDouble(xmlActivation, "Threshold");
                    int maxIters = XmlUtils.GetValueAsInt(xmlActivation, "MaxIters");
                    return NetworkActivationScheme.CreateCyclicRelaxingActivationScheme(deltaThreshold, maxIters);
            }
            throw new ArgumentException($"Invalid or missing ActivationScheme XML config setting [{schemeStr}]");
        }
    }
}
