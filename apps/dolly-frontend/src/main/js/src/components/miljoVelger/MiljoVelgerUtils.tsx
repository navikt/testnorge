export const gyldigeDollyMiljoer = (dollymiljoer: any) => {
	if (!dollymiljoer) return []
	if (dollymiljoer.Q) return dollymiljoer.Q.filter((env: any) => env.id !== 'qx')
	return dollymiljoer
}

export const filterMiljoe = (dollyMiljoe, utvalgteMiljoer, tilgjengeligeMiljoer = null) => {
	if (!utvalgteMiljoer) return []
	const dollyMiljoeArray = dollyMiljoe?.map((miljoe) => miljoe?.id)
	//Filtrerer bort de miljøene som er tilgjengelige for fagsystemene eller en mal,
	//men ikke Dolly per dags dato
	return utvalgteMiljoer.filter(
		(miljoe) =>
			dollyMiljoeArray.includes(miljoe) &&
			(!tilgjengeligeMiljoer || tilgjengeligeMiljoer.includes(miljoe)),
	)
}
