export const fixNamesAndDuplicatesOfStatus = (statusListe: []) => {
	return [
		...new Map(
			statusListe
				.map((status: { navn: string; miljo: string }) => {
					if (status.navn?.includes('Testnav TPS messaging')) {
						status.navn = 'Tjenestebasert personsystem (TPS)'
					}
					return status
				})
				.map((v) => [JSON.stringify([v.navn, v.miljo]), v])
		).values(),
	]
}
