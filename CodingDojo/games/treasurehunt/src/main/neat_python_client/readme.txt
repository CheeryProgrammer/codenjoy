1. Установить python (тестировал на 3.7.3).
# Дальнейшая установка согласно мануалу: https://pyjnius.readthedocs.io/en/stable/installation.html#installation-for-windows
2. Установить java jdk+jre (тестировал на 1.8.0_202)
3. Установить Microsoft Visual C++ Build Tools (были установлены с Visual Studio)
4. Установить пакеты питона:
	- обновить pip и setuptools:	python -m pip install --upgrade pip setuptools
	- установить Cython:			python -m pip install --upgrade cython
	- установить Pyjnius:			pip install pyjnius

5. Пример запуска и использования игрового движка - в скрипте test.py