import React, { useEffect } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { AdvancedOptions } from '~/pages/testnorgePage/search/advancedOptions/AdvancedOptions'
import { DollyApi } from '~/service/Api'

const paths = {
	statsborgerskap: 'nasjonalitet.statsborgerskap',
	fraflyttingsland: 'nasjonalitet.innflytting.fraflyttingsland',
	histFraflyttingsland: 'nasjonalitet.innflytting.historiskFraflyttingsland',
	tilflyttingsland: 'nasjonalitet.utflytting.tilflyttingsland',
	histTilflyttingsland: 'nasjonalitet.utflytting.historiskTilflyttingsland',
}

export const Nasjonalitet = () => {
	const landOptions = [
		{ value: 'VERDEN', label: 'Vilkårlig land' },
		{ value: 'EØS', label: 'EØS-området' },
		{ value: 'UEØS', label: 'Utenfor EØS-området' },
	]

	// useEffect(() => {
	// 	DollyApi.getKodeverkByNavn('landkoder').then()
	// 	malerApi
	// 		.hentMaler()
	// 		.then((data) => {
	// 			setMaler(data.malbestillinger[`${brukerId}`] || [])
	// 		})
	// 		.then(() => setLoading(false))
	// }, [])

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
			/>
			<FormikSelect
				name={paths.tilflyttingsland}
				label="Utflyttet fra Norge til"
				options={landOptions}
				optionHeight={50}
				size="medium"
			/>
			<AdvancedOptions>
				<FormikSelect
					name={paths.histFraflyttingsland}
					label="Tidligere innflyttet fra"
					options={landOptions}
					optionHeight={50}
					size="medium"
					info="Velg hvor person har tildligere innflyttet til Norge fra."
				/>
				<FormikSelect
					name={paths.histTilflyttingsland}
					label="Tidligere utflyttet til"
					options={landOptions}
					optionHeight={50}
					size="medium"
					info="Velg hvor person har tildligere utflyttet fra Norge til."
				/>
			</AdvancedOptions>
		</section>
	)
}

export const NasjonalitetPaths = Object.values(paths)
