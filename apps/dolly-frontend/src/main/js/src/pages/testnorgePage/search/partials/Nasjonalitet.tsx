import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '~/config/kodeverk'
import { AdvancedOptions } from '~/pages/testnorgePage/search/advancedOptions/AdvancedOptions'
import { DollyApi } from '~/service/Api'

const paths = {
	statsborgerskap: 'nasjonalitet.statsborgerskap',
	fraflyttingsland: 'nasjonalitet.innflytting.fraflyttingsland',
	tilflyttingsland: 'nasjonalitet.utflytting.tilflyttingsland',
	histFraflyttingsland: 'nasjonalitet.innflytting.historiskFraflyttingsland',
	histTilflyttingsland: 'nasjonalitet.utflytting.historiskTilflyttingsland',
}

const initalLandOptions = [
	{ value: 'VERDEN', label: 'VILKÅRLIG LAND' },
	{ value: 'EU', label: 'EØS-OMRÅDET' },
	{ value: 'U-EU', label: 'UTENFOR EØS-OMRÅDET' },
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
			<AdvancedOptions>
				<FormikSelect
					name={paths.histFraflyttingsland}
					label="Tidligere innvandret fra"
					options={landOptions}
					optionHeight={50}
					size="medium"
					info="Velg hvor person har tildligere innvandret til Norge fra."
					isLoading={loading}
				/>
				<FormikSelect
					name={paths.histTilflyttingsland}
					label="Tidligere utflyttet til"
					options={landOptions}
					optionHeight={50}
					size="medium"
					info="Velg hvor person har tildligere utflyttet fra Norge til."
					isLoading={loading}
				/>
			</AdvancedOptions>
		</section>
	)
}

export const NasjonalitetPaths = Object.values(paths)
