FROM ghcr.io/danger/danger-kotlin:0.7.1

COPY Dangerfile.df.kts /Dangerfile.df.kts
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
