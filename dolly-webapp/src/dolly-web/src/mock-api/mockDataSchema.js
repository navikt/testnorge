const schema = {
    "type": "object",
    "properties": {
        "persons": {
            type: "array",
            minItems: 10,
            maxItems: 10,
            items: {
                type: "object",
                properties: {
                    id: {
                        "type": "integer",
                        "autoIncrement": true
                    },
                    fnr: {
                        type: 'string',
                        pattern: '\\d{11}'
                    },
                    fornavn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                    etternavn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                    kjonn: {
                        type: 'string',
                        pattern: '[mk]{1}'
                    },
                    adresse: {
                        type: "string",
                        pattern: '[a-z]{1,9}'
                    },
                    gruppeId: {
                        tyoe: "string",
                        pattern: '[1-5]'
                    }

                },
                required: ["id","fnr", "kjonn", "fornavn", "etternavn", "adresse", "gruppeId"]
            }
        },
        "grupper": {
            type: "array",
            minItems: 5,
            maxItems: 5,
            items: {
                type: "object",
                properties: {
                    id: {
                        "type": "integer",
                        "autoIncrement": true
                    },
                    navn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                },
                required: ["id", "navn"]
            }
        },
        "teams": {
            type: "array",
            minItems: 2,
            maxItems: 10,
            items: {
                type: "object",
                properties: {
                    id: {
                        "type": "integer",
                        "autoIncrement": true
                    },
                    navn: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                    beskrivelse: {
                        type: 'string',
                        pattern: '[a-z]{1,9}'
                    },
                },
                required: ["id", "navn", "beskrivelse"]
            }
        },
        "bruker": {
            type: "object",
            properties: {
                id: {
                    "type": "integer",
                    "autoIncrement": true
                },
                navn: "testBruker"
            }
        }
    },
    "required": ["persons", "grupper", "teams","bruker"]
};

module.exports = schema;