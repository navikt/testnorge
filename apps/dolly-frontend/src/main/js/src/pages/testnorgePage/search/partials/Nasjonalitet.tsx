import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const paths = {
	statsborgerskap: 'nasjonalitet.statsborgerskap',
	inflyttet: 'nasjonalitet.innflyttingTilNorge',
	fraflyttingsland: 'nasjonalitet.innflytting.fraflyttingsland',
	histFraflyttingsland: 'nasjonalitet.innflytting.historiskFraflyttingsland',
	utflyttet: 'nasjonalitet.utflyttingFraNorge',
	tilflyttingsland: 'nasjonalitet.innflytting.tilflyttingsland',
	histTilflyttingsland: 'nasjonalitet.innflytting.historiskTilflyttingsland',
}

export const Nasjonalitet = () => {
	return (
		<section>
			<FormikSelect
				name={paths.statsborgerskap}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				optionHeight={50}
				size="medium"
			/>
			{/*<FormikCheckbox name={paths.inflyttet} label="Har innflyttet til Norge" size="medium" />*/}
			{/*<FormikCheckbox name={paths.utflyttet} label="Har utflyttet fra Norge" size="medium" />*/}
			<FormikSelect
				name={paths.fraflyttingsland}
				label="Innflyttet til Norge fra"
				kodeverk={'Landkoder'}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={paths.tilflyttingsland}
				label="Utflyttet fra Norge til"
				kodeverk={'Landkoder'}
				optionHeight={50}
				size="medium"
			/>
		</section>
	)
}

export const NasjonalitetPaths = Object.values(paths)
