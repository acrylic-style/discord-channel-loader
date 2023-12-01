# discord-channel-loader

Loads discord channel messages into [vector database](https://github.com/azisaba/generic-vector-search).

## Environment variables

- TOKEN - Discord bot token
- CHANNELS - Set of channel ids to load messages from
- LOAD_ALL - If not empty, loads all messages from the channel. If false, loads only messages after the bot started.
- ENDPOINT - Endpoint of vector database
- SECRET - Secret key of vector database
