import React, { useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { AdvancedOptions } from '~/pages/testnorgePage/search/advancedOptions/AdvancedOptions'
import { DollyApi } from '~/service/Api'
import { Exception } from 'sass'

const paths = {
	statsborgerskap: 'nasjonalitet.statsborgerskap',
	fraflyttingsland: 'nasjonalitet.innflytting.fraflyttingsland',
	histFraflyttingsland: 'nasjonalitet.innflytting.historiskFraflyttingsland',
	tilflyttingsland: 'nasjonalitet.utflytting.tilflyttingsland',
	histTilflyttingsland: 'nasjonalitet.utflytting.historiskTilflyttingsland',
}

const initalLandOptions = [
	{ value: 'VERDEN', label: 'VILKÅRLIG LAND' },
	{ value: 'EØS', label: 'EØS-OMRÅDET' },
	{ value: 'UEØS', label: 'UTENFOR EØS-OMRÅDET' },
]

export const Nasjonalitet = () => {
	const [landOptions, setLandOptions] = useState([])
	const [loading, setLoading] = useState(false)

	useEffect(() => {
		setLoading(true)
		DollyApi.getKodeverkByNavn('Landkoder')
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
				label="Innflyttet til Norge fra"
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
					label="Tidligere innflyttet fra"
					options={landOptions}
					optionHeight={50}
					size="medium"
					info="Velg hvor person har tildligere innflyttet til Norge fra."
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
