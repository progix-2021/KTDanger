FROM node:12-slim
RUN npm install -g danger

COPY Dangerfile.ts /Dangerfile.ts
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
