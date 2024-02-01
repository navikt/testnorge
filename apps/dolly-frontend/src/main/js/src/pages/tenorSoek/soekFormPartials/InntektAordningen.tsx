import Accordion from '@navikt/ds-react/src/accordion/Accordion'
import React, { SyntheticEvent } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { createOptions } from '@/pages/tenorSoek/utils'
import { Header, SoekKategori } from '@/components/ui/soekForm/SoekForm'
import * as _ from 'lodash-es'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const InntektAordningen = ({ formikBag, handleChange, handleChangeList, getValue }: any) => {
	const { domain: inntektstypeOptions } = useTenorDomain('Inntektstype')
	const { domain: beskrivelseOptions } = useTenorDomain('AOrdningBeskrivelse')
	const { domain: forskuddstrekkOptions } = useTenorDomain('Forskuddstrekk')
	console.log('formikBag: ', formikBag.values) //TODO - SLETT MEG
	return (
		<SoekKategori>
			<Monthpicker
				name="inntekt.periode.fraOgMed"
				label="Periode f.o.m. dato"
				handleDateChange={(val: Date) => {
					handleChange(val ? val.toISOString().substr(0, 7) : '', 'inntekt.periode.fraOgMed')
				}}
				date={getValue('inntekt.periode.fraOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<Monthpicker
				name="inntekt.periode.tilOgMed"
				label="Periode t.o.m. dato"
				handleDateChange={(val: Date) => {
					handleChange(val ? val.toISOString().substr(0, 7) : '', 'inntekt.periode.tilOgMed')
				}}
				date={getValue('inntekt.periode.tilOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikTextInput
				name="inntekt.opplysningspliktig"
				label="Opplysningspliktig org.nr."
				onBlur={(val: SyntheticEvent) =>
					handleChange(val?.target?.value || null, 'inntekt.opplysningspliktig')
				}
				// value={getValue('inntekt.opplysningspliktig')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormikSelect
					name="inntekt.inntektstyper"
					options={createOptions(inntektstypeOptions?.data)}
					isMulti={true}
					size="grow"
					label="Inntektstyper"
					// placeholder="Velg beskrivelse ..."
					onChange={(val: SyntheticEvent) => handleChangeList(val || null, 'inntekt.inntektstyper')}
					value={getValue('inntekt.inntektstyper')}
				/>
				<FormikSelect
					name="inntekt.forskuddstrekk"
					options={createOptions(forskuddstrekkOptions?.data)}
					isMulti={true}
					size="grow"
					label="Forskuddstrekk"
					// placeholder="Velg beskrivelse ..."
					onChange={(val: SyntheticEvent) =>
						handleChangeList(val || null, 'inntekt.forskuddstrekk')
					}
					value={getValue('inntekt.forskuddstrekk')}
				/>
			</div>
			<FormikSelect
				name="inntekt.beskrivelse"
				options={createOptions(beskrivelseOptions?.data)}
				size="large"
				label="Beskrivelse"
				// placeholder="Velg beskrivelse ..."
				// onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'inntekt.beskrivelse')}
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'inntekt.beskrivelse')}
				value={getValue('inntekt.beskrivelse')}
			/>
			{/*<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>*/}
			{/*	*/}
			{/*</div>*/}
			<FormikSelect
				name="inntekt.harHistorikk"
				options={Options('boolean')}
				size="small"
				label="Har historikk"
				// placeholder="Velg beskrivelse ..."
				onChange={(val: boolean) => handleChange(val?.value, 'inntekt.harHistorikk')}
				value={getValue('inntekt.harHistorikk')}
			/>
		</SoekKategori>
	)
}
