import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { TestComponentSelectors } from '#/mocks/Selectors'

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
			<FormTextInput
				name="identifikator"
				label="Fødselsnummer / D-nummer"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'identifikator')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="identifikatorType"
				options={createOptions(identifikatorTypeOptions?.data, true)}
				label="Identifikatortype"
				onChange={(val: any) => handleChange(val?.value || null, 'identifikatorType')}
			/>
			<FormDatepicker
				name="foedselsdato.fraOgMed"
				label="Fødselsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="foedselsdato.tilOgMed"
				label="Fødselsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'foedselsdato.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="doedsdato.fraOgMed"
				label="Dødsdato f.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="doedsdato.tilOgMed"
				label="Dødsdato t.o.m."
				onChange={(val: SyntheticEvent) => handleChange(val || null, 'doedsdato.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="kjoenn"
				options={createOptions(kjoennOptions?.data)}
				label="Kjønn"
				onChange={(val: any) => handleChange(val?.value || null, 'kjoenn')}
			/>
			<FormSelect
				name="personstatus"
				options={createOptions(personstatusOptions?.data)}
				label="Personstatus"
				onChange={(val: any) => handleChange(val?.value || null, 'personstatus')}
			/>
			<FormSelect
				name="sivilstand"
				options={createOptions(sivilstatusOptions?.data)}
				label="Sivilstand"
				onChange={(val: any) => handleChange(val?.value || null, 'sivilstand')}
			/>
			<FormSelect
				name="identitetsgrunnlagStatus"
				options={createOptions(identitetsgrunnlagStatusOptions?.data)}
				label="Identitetsgrunnlagsstatus"
				onChange={(val: any) => handleChange(val?.value || null, 'identitetsgrunnlagStatus')}
			/>
			<FormSelect
				name="adressebeskyttelse"
				options={createOptions(adressebeskyttelseOptions?.data)}
				label="Adressebeskyttelse"
				onChange={(val: any) => handleChange(val?.value || null, 'adressebeskyttelse')}
			/>
			<FormSelect
				name="harFalskIdentitet"
				options={Options('boolean')}
				label="Har falsk identitet"
				onChange={(val: any) => handleChange(val?.value, 'harFalskIdentitet')}
			/>
			<div className="flexbox--full-width">
				<FormSelect
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
			<FormCheckbox
				name="harLegitimasjonsdokument"
				data-testid={TestComponentSelectors.CHECKBOX_TENORSOEK}
				label="Har legitimasjonsdokument"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harLegitimasjonsdokument')
				}
			/>
		</SoekKategori>
	)
}
