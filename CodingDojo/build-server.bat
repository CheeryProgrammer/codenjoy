set ROOT=%CD%

set GAMES_TO_RUN=eatordie

IF "%GAMES_TO_RUN%"=="" (
    call %ROOT%\mvnw clean install -DskipTests
    
    cd %ROOT%\server
    call %ROOT%\mvnw clean package -DskipTests -DallGames
) else (
    cd %ROOT%\games
    call mvn clean install -N
    
    cd %ROOT%\games\engine
    call mvn clean install -DskipTests
    
    for %%a in ("%GAMES_TO_RUN:,=" "%") do (
        cd %ROOT%\games\%%~a
        call mvn clean install -DskipTests
    )
    
    cd %ROOT%\server
    call mvn clean package -DskipTests -P%GAMES_TO_RUN%
)

cd %ROOT%