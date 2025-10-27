# Adding logout functionality
Exactly! You’ve got it perfectly. Let me just rephrase it step by step to make it crystal clear:

1. **Incoming request with token** → `isBlacklisted(token)` is called first.

2. **Inside `isBlacklisted`**:

    * If the token **isn’t in the map**, it’s obviously not blacklisted → return `false`.
    * If the token **is in the map**, check its stored expiration (`expiryEpochSeconds`):

        * If the current time is **past that expiration**, remove it from the map and return `false` (token isn’t blacklisted anymore, but by this time the JWT itself would likely be expired anyway).
        * If the current time is **before that expiration**, return `true` → the token is blacklisted, reject the request.

3. **JWT validation after that**:

    * If `isBlacklisted` returned `false`, you then check whether the JWT itself is expired.
    * If the JWT is expired → the standard JWT validation fails, request denied.
    * If the JWT is valid → request proceeds normally.

So the blacklist essentially acts as an **early override**: it allows you to immediately invalidate tokens **before their natural JWT expiry**, while still cleaning itself up automatically once the JWT would have expired anyway.

---

If you want, I can draw a **small flow diagram** showing exactly how `isBlacklisted` + JWT validation work together — it makes this super intuitive. Do you want me to do that?
