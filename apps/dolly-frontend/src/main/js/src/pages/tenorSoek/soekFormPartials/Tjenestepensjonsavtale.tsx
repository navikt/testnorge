import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'

// TODO: Fiks begge feltene!
export const Tjenestepensjonsavtale = ({ handleChange, getValue }: any) => {
	return (
		<SoekKategori>
			<FormTextInput
				name="tjenestepensjonsavtale.pensjonsinnretningOrgnr"
				label="Pensjonsinnretning (org.nr.)"
				// type="number"
				// @ts-ignore
				onBlur={(val: any) =>
					handleChange(
						val?.target?.value ?? null,
						'tjenestepensjonsavtale.pensjonsinnretningOrgnr',
						`Tjenestepensjonsavtale org.nr.: ${val?.target?.value}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<Monthpicker
				name="tjenestepensjonsavtale.periode"
				label="Periode"
				// @ts-ignore
				handleDateChange={(val: Date) => {
					console.log('val: ', val) //TODO - SLETT MEG
					handleChange(
						val ? val.toISOString().substring(0, 7) : '',
						'tjenestepensjonsavtale.periode',
						`Tjenestepensjonsavtale periode: ${val?.toISOString().substring(0, 7)}`,
					)
				}}
				date={getValue('tjenestepensjonsavtale.periode')}
			/>
		</SoekKategori>
	)
}
