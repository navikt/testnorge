import React from 'react'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { antallResultatOptions } from '~/pages/soekMiniNorge/search/utils'
import { OptionsSection } from '~/pages/testnorgePage/search/options/OptionsSection'
import { getCount, SearchOptionsProps } from '~/pages/testnorgePage/search/SearchOptions'
import {
	Ident,
	IdentPaths,
	Nasjonalitet,
	NasjonalitetPaths,
	Boadresse,
	BoadressePaths,
	Diverse,
	DiversePaths,
} from './Options'

export const SearchOptions = (props: SearchOptionsProps) => (
	<>
		<FormikSelect
			name="antallResultat"
			label="Maks antall resultat"
			options={antallResultatOptions}
		/>

		<h2>Personinformasjon</h2>
		<OptionsSection
			heading="Ident"
			options={<Ident />}
			startOpen={true}
			numSelected={getCount(IdentPaths, props.formikBag)}
		/>
		<OptionsSection
			heading="Nasjonalitet"
			options={<Nasjonalitet />}
			numSelected={getCount(NasjonalitetPaths, props.formikBag)}
		/>
		<OptionsSection
			heading="Boadresse"
			options={<Boadresse />}
			numSelected={getCount(BoadressePaths, props.formikBag)}
		/>
		<OptionsSection
			heading="Diverse"
			options={<Diverse />}
			numSelected={getCount(DiversePaths, props.formikBag)}
		/>
	</>
)
