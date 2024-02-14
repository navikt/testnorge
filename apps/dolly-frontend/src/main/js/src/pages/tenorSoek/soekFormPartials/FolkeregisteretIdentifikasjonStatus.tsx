import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'

export const FolkeregisteretIdentifikasjonStatus = ({
	formikBag,
	handleChange,
	handleChangeList,
	handleChangeBoolean,
	getValue,
}: any) => {
	const { domain: identifikatorTypeOptions } = useTenorDomain('IdentifikatorType')
	const { domain: kjoennOptions } = useTenorDomain('Kjoenn')
	const { domain: personstatusOptions } = useTenorDomain('Personstatus')
	const { domain: sivilstatusOptions } = useTenorDomain('Sivilstatus')
	const { domain: utenlandskPersonIdentifikasjonOptions } = useTenorDomain(
		'UtenlandskPersonIdentifikasjon',
	)
	const { domain: identitetsgrunnlagStatusOptions } = useTenorDomain('IdentitetsgrunnlagStatus')
	const { domain: adressebeskyttelseOptions } = useTenorDomain('Adressebeskyttelse')

	return (
		<SoekKategori>
			<FormikTextInput
				name="identifikator"
				label="Fødselsnummer / D-nummer"
				onBlur={(val: SyntheticEvent) => handleChange(val?.target?.value || null, 'identifikator')}
				visHvisAvhuket={false}
				// fastfield={false}
			/>
			<FormikSelect
				name="identifikatorType"
				options={createOptions(identifikatorTypeOptions?.data)}
				// size="medium"
				label="Identifikatortype"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'identifikatorType')}
				value={getValue('identifikatorType')}
			/>
			<FormikDatepicker
				name="foedselsdato.fraOgMed"
				label="Fødselsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.fraOgMed')}
				date={getValue('foedselsdato.fraOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="foedselsdato.tilOgMed"
				label="Fødselsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.tilOgMed')}
				date={getValue('foedselsdato.tilOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="doedsdato.fraOgMed"
				label="Dødsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.fraOgMed')}
				date={getValue('doedsdato.fraOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikDatepicker
				name="doedsdato.tilOgMed"
				label="Dødsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.tilOgMed')}
				date={getValue('doedsdato.tilOgMed')}
				visHvisAvhuket={false}
				fastfield={false}
			/>
			<FormikSelect
				name="kjoenn"
				options={createOptions(kjoennOptions?.data)}
				// size="medium"
				label="Kjønn"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'kjoenn')}
				value={getValue('kjoenn')}
			/>
			<FormikSelect
				name="personstatus"
				options={createOptions(personstatusOptions?.data)}
				// size="medium"
				label="Personstatus"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'personstatus')}
				value={getValue('personstatus')}
			/>
			<FormikSelect
				name="sivilstand"
				options={createOptions(sivilstatusOptions?.data)}
				// size="medium"
				label="Sivilstand"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'sivilstand')}
				value={getValue('sivilstand')}
			/>
			<div className="flexbox--full-width" style={{ fontSize: 'medium' }}>
				<FormikSelect
					name="utenlandskPersonIdentifikasjon"
					options={createOptions(utenlandskPersonIdentifikasjonOptions?.data)}
					isMulti={true}
					size="grow"
					label="Utenlandsk identifikasjonsnummertype"
					onChange={(val: SyntheticEvent) =>
						handleChangeList(val || null, 'utenlandskPersonIdentifikasjon')
					}
					value={getValue('utenlandskPersonIdentifikasjon')}
				/>
			</div>
			<FormikSelect
				name="identitetsgrunnlagStatus"
				options={createOptions(identitetsgrunnlagStatusOptions?.data)}
				// size="medium"
				label="Identitetsgrunnlagsstatus"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'identitetsgrunnlagStatus')
				}
				value={getValue('identitetsgrunnlagStatus')}
			/>
			<FormikSelect
				name="adressebeskyttelse"
				options={createOptions(adressebeskyttelseOptions?.data)}
				// size="medium"
				label="Adressebeskyttelse"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'adressebeskyttelse')}
				value={getValue('adressebeskyttelse')}
			/>
			{/*<FormikCheckbox*/}
			{/*	name="harLegitimasjonsdokument"*/}
			{/*	label="Har legitimasjonsdokument"*/}
			{/*	onChange={(val: SyntheticEvent) =>*/}
			{/*		handleChangeBoolean(val?.target?.checked, 'harLegitimasjonsdokument')*/}
			{/*	}*/}
			{/*	value={getValue('harLegitimasjonsdokument')}*/}
			{/*/>*/}
			{/*TODO: checkbox???*/}
			<FormikSelect
				name="harLegitimasjonsdokument"
				options={Options('boolean')}
				size="small"
				label="Har legitimasjonsdokument"
				onChange={(val: boolean) => handleChangeBoolean(val?.value, 'harLegitimasjonsdokument')}
				value={getValue('harLegitimasjonsdokument')}
			/>
			{/*<FormikCheckbox*/}
			{/*	name="harFalskIdentitet"*/}
			{/*	label="Har falsk identitet"*/}
			{/*	onChange={(val: SyntheticEvent) =>*/}
			{/*		handleChangeBoolean(val?.target?.checked, 'harFalskIdentitet')*/}
			{/*	}*/}
			{/*	value={getValue('harFalskIdentitet')}*/}
			{/*/>*/}
			<FormikSelect
				name="harFalskIdentitet"
				options={Options('boolean')}
				size="small"
				label="Har falsk identitet"
				onChange={(val: boolean) => handleChangeBoolean(val?.value, 'harFalskIdentitet')}
				value={getValue('harFalskIdentitet')}
			/>
			{/*<FormikCheckbox*/}
			{/*	name="harNorskStatsborgerskap"*/}
			{/*	label="Har norsk statsborgerskap"*/}
			{/*	onChange={(val: SyntheticEvent) =>*/}
			{/*		handleChangeBoolean(val?.target?.checked, 'harNorskStatsborgerskap')*/}
			{/*	}*/}
			{/*	value={getValue('harNorskStatsborgerskap')}*/}
			{/*/>*/}
		</SoekKategori>
	)
}
