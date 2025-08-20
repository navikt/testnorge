import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const InntektAordningen = ({ handleChange, handleChangeList, getValue }: any) => {
	const { domain: inntektstypeOptions } = useTenorDomain('Inntektstype')
	const { domain: beskrivelseOptions } = useTenorDomain('AOrdningBeskrivelse')
	const { domain: forskuddstrekkOptions } = useTenorDomain('Forskuddstrekk')

	return (
		<SoekKategori>
			<Monthpicker
				name="inntekt.periode.fraOgMed"
				label="Periode f.o.m. dato"
				// @ts-ignore
				handleDateChange={(val: Date) => {
					handleChange(val ? val.toISOString().substring(0, 7) : '', 'inntekt.periode.fraOgMed')
				}}
				date={getValue('inntekt.periode.fraOgMed')}
			/>
			<Monthpicker
				name="inntekt.periode.tilOgMed"
				label="Periode t.o.m. dato"
				// @ts-ignore
				handleDateChange={(val: Date) => {
					handleChange(val ? val.toISOString().substring(0, 7) : '', 'inntekt.periode.tilOgMed')
				}}
				date={getValue('inntekt.periode.tilOgMed')}
			/>
			<FormTextInput
				name="inntekt.opplysningspliktig"
				label="Opplysningspliktig org.nr."
				// @ts-ignore
				onBlur={(val: any) =>
					handleChange(val?.target?.value || null, 'inntekt.opplysningspliktig')
				}
				visHvisAvhuket={false}
			/>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormSelect
					name="inntekt.inntektstyper"
					options={createOptions(inntektstypeOptions?.data)}
					isMulti={true}
					size="grow"
					label="Inntektstyper"
					onChange={(val: SyntheticEvent) => handleChangeList(val || null, 'inntekt.inntektstyper')}
				/>
				<FormSelect
					name="inntekt.forskuddstrekk"
					options={createOptions(forskuddstrekkOptions?.data)}
					isMulti={true}
					size="grow"
					label="Forskuddstrekk"
					onChange={(val: SyntheticEvent) =>
						handleChangeList(val || null, 'inntekt.forskuddstrekk')
					}
				/>
			</div>
			<FormSelect
				name="inntekt.beskrivelse"
				options={createOptions(beskrivelseOptions?.data)}
				size="xlarge"
				label="Beskrivelse"
				onChange={(val: any) => handleChange(val?.value || null, 'inntekt.beskrivelse')}
			/>
			<FormSelect
				name="inntekt.harHistorikk"
				options={Options('boolean')}
				size="small"
				label="Har historikk"
				onChange={(val: any) => handleChange(val?.value, 'inntekt.harHistorikk')}
			/>
		</SoekKategori>
	)
}
