if [ -d "/app/partner/server_bak" ];
  then
    rm -Rf "/app/partner/server_bak"
    mv "/app/partner/server" "/app/partner/server_bak"
  else
    mv "/app/partner/server" "/app/partner/server_bak"
fi