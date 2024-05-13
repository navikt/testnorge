import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React, { SyntheticEvent } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const Tjenestepensjonsavtale = ({ handleChange }: any) => (
	<SoekKategori>
		<FormTextInput
			name="tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr"
			label="Manglende grunnlagsdata"
			onBlur={(val: any) =>
				handleChange(
					val?.target?.value || undefined,
					'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr',
				)
			}
		/>
		<FormDatepicker
			name="tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode.fraOgMed"
			label="Periode f.o.m."
			onChange={(val: SyntheticEvent) =>
				handleChange(
					val || null,
					'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode.fraOgMed',
				)
			}
			visHvisAvhuket={false}
		/>
		<FormDatepicker
			name="tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode.tilOgMed"
			label="Periode t.o.m."
			onChange={(val: SyntheticEvent) =>
				handleChange(
					val || null,
					'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode.tilOgMed',
				)
			}
			visHvisAvhuket={false}
		/>
	</SoekKategori>
)
