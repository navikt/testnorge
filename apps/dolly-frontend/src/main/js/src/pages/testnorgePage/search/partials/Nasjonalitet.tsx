import React, { useEffect, useState } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '@/config/kodeverk'
import { DollyApi } from '@/service/Api'

const paths = {
	statsborgerskap: 'nasjonalitet.statsborgerskap',
	fraflyttingsland: 'nasjonalitet.innflytting.fraflyttingsland',
	tilflyttingsland: 'nasjonalitet.utflytting.tilflyttingsland',
}

const initalLandOptions = [
	{ value: 'V', label: 'VILKÅRLIG LAND' },
	{ value: 'E', label: 'EØS-OMRÅDET' },
	{ value: 'U', label: 'UTENFOR EØS-OMRÅDET' },
]

export const Nasjonalitet = () => {
	const [landOptions, setLandOptions] = useState(initalLandOptions)
	const [loading, setLoading] = useState(false)

	useEffect(() => {
		setLoading(true)
		DollyApi.getKodeverkByNavn(GtKodeverk.LAND)
			.then((data: any) => {
				const land = data?.data?.koder
				const nyOptions = [...initalLandOptions, ...land]
				setLandOptions(nyOptions)
				setLoading(false)
			})
			.catch((_error) => {
				setLandOptions(initalLandOptions)
				setLoading(false)
			})
	}, [])

	return (
		<section>
			<FormikSelect
				name={paths.statsborgerskap}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={paths.fraflyttingsland}
				label="Innvandret til Norge fra"
				options={landOptions}
				optionHeight={50}
				size="medium"
				isLoading={loading}
			/>
			<FormikSelect
				name={paths.tilflyttingsland}
				label="Utflyttet fra Norge til"
				options={landOptions}
				optionHeight={50}
				size="medium"
				isLoading={loading}
			/>
		</section>
	)
}

export const NasjonalitetPaths = Object.values(paths)
