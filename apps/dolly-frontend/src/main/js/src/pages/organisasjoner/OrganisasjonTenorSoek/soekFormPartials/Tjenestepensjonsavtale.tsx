import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { format } from 'date-fns'

export const Tjenestepensjonsavtale = ({ handleChange }: any) => (
	<SoekKategori>
		<h4>Søk etter opplysningspliktige enheter som har avtale med pensjonsinnretning</h4>
		<FormTextInput
			visHvisAvhuket={false}
			name="tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr"
			label="Pensjonsinnretning orgnr"
			placeholder={'9 siffer'}
			onBlur={(val: any) =>
				handleChange(
					val?.target?.value || null,
					'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.tjenestepensjonsinnretningOrgnr',
				)
			}
		/>
		<Monthpicker
			name="tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode"
			label="Periode"
			onChange={(val: Date) => {
				handleChange(
					val ? format(val, 'yyyy-MM') : undefined,
					'tenorRelasjoner.tjenestepensjonsavtaleOpplysningspliktig.periode',
				)
			}}
		/>
	</SoekKategori>
)
