export const fixNamesAndDuplicatesOfStatus = (statusListe: []) => {
	return [
		...new Map(
			statusListe
				.map(
					(status: {
						id: string
						navn: string
						miljo: string
						melding: string
						orgnummer: string
					}) => {
						if (status.id?.includes('TPS_MESSAGING')) {
							status.navn = 'Tjenestebasert personsystem (TPS)'
						}
						return status
					}
				)
				.map((v) => [JSON.stringify([v.id, v.navn, v.miljo]), v])
		).values(),
	]
}
