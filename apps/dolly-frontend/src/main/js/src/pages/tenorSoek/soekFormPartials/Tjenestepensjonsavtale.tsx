import React from 'react'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'

export const Tjenestepensjonsavtale = ({ handleChange, getValue }: any) => {
	return (
		<SoekKategori>
			<FormTextInput
				name="tjenestepensjonsavtale.pensjonsinnretningOrgnr"
				label="Pensjonsinnretning (org.nr.)"
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
