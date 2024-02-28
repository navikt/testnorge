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

export const FolkeregisteretIdentifikasjonStatus = ({ handleChange, handleChangeList }: any) => {
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
			/>
			<FormikSelect
				name="identifikatorType"
				options={createOptions(identifikatorTypeOptions?.data)}
				label="Identifikatortype"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'identifikatorType')}
			/>
			<FormikDatepicker
				name="foedselsdato.fraOgMed"
				label="Fødselsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormikDatepicker
				name="foedselsdato.tilOgMed"
				label="Fødselsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormikDatepicker
				name="doedsdato.fraOgMed"
				label="Dødsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormikDatepicker
				name="doedsdato.tilOgMed"
				label="Dødsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormikSelect
				name="kjoenn"
				options={createOptions(kjoennOptions?.data)}
				label="Kjønn"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'kjoenn')}
			/>
			<FormikSelect
				name="personstatus"
				options={createOptions(personstatusOptions?.data)}
				label="Personstatus"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'personstatus')}
			/>
			<FormikSelect
				name="sivilstand"
				options={createOptions(sivilstatusOptions?.data)}
				label="Sivilstand"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'sivilstand')}
			/>
			<FormikSelect
				name="identitetsgrunnlagStatus"
				options={createOptions(identitetsgrunnlagStatusOptions?.data)}
				label="Identitetsgrunnlagsstatus"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.value || null, 'identitetsgrunnlagStatus')
				}
			/>
			<FormikSelect
				name="adressebeskyttelse"
				options={createOptions(adressebeskyttelseOptions?.data)}
				label="Adressebeskyttelse"
				onChange={(val: SyntheticEvent) => handleChange(val?.value || null, 'adressebeskyttelse')}
			/>
			<FormikSelect
				name="harFalskIdentitet"
				options={Options('boolean')}
				label="Har falsk identitet"
				onChange={(val: boolean) => handleChange(val?.value, 'harFalskIdentitet')}
			/>
			<div className="flexbox--full-width">
				<FormikSelect
					name="utenlandskPersonIdentifikasjon"
					options={createOptions(utenlandskPersonIdentifikasjonOptions?.data)}
					isMulti={true}
					size="grow"
					label="Utenlandsk identifikasjonsnummertype"
					onChange={(val: SyntheticEvent) =>
						handleChangeList(val || null, 'utenlandskPersonIdentifikasjon')
					}
				/>
			</div>
			<FormikCheckbox
				name="harLegitimasjonsdokument"
				label="Har legitimasjonsdokument"
				onChange={(val: SyntheticEvent) =>
					handleChange(val?.target?.checked || undefined, 'harLegitimasjonsdokument')
				}
			/>
		</SoekKategori>
	)
}
