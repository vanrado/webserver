function function_showAssignment(){
  less docs/zadanie.txt
}

function function_showDocumentation(){
  less docs/dokumentacia.txt
}

function function_runServer(){
  export CLASSPATH=${CLASSPATH}:./bin/
  #standardne hodnoty
  PORT="15000"
  WEBROOT="webroot"
  ODP=""
  
  echo -e "Prajete si specifikovat port servera? (ano/nie)"
  read ODP
  if [ "$ODP" == "ano" ];
  then
    echo -e "Specifikujte prosim port servera: "
    read PORT
  else
    echo -e "Bude pouzity standardny port $PORT"
  fi
  
  echo -e "Prajete si specifikovat korenovy adresar?(ano/nie)"
  read ODP
  if [ "$ODP" == "ano" ];
  then
    echo -e "Specifikujte korenovy adresar: "
    read WEBROOT
  else
    echo -e "Bude pouzity standardny korenovy adresar $WEBROOT"
  fi
  
  konsole 2>console_log.txt -e java src.ServerApplication $PORT $WEBROOT
}

function function_shutdownServer(){
  export CLASSPATH=${CLASSPATH}:./bin/shutdown/
  konsole 2>console_log.txt -e java shutdown.Shutdown
}

function function_menu(){
  while true;
  do
    echo -e "\n###### Menu ######"
    echo -e "1 Zobraz zadanie"
    echo -e "2 Zobraz dokumentáciu"
    echo -e "3 Spusť server"
    echo -e "4 Ukonči server"
    echo -e "0 Ukonci"
    
    read reply    
    case $reply in 
      "1" )
	function_showAssignment
	;;
      "0" )
	break;
	;;
      "2" )
	function_showDocumentation
	;;
      "3" )
	function_runServer
	;;
      "4" )
	function_shutdownServer
	;;
    esac
  done
}
