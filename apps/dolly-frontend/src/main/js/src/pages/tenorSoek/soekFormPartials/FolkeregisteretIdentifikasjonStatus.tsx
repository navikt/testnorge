import { useTenorDomain } from '@/utils/hooks/useTenorSoek'
import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import React, { SyntheticEvent } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { createOptions } from '@/pages/tenorSoek/utils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { codeToNorskLabel, formatDate, oversettBoolean } from '@/utils/DataFormatter'

export const FolkeregisteretIdentifikasjonStatus = ({ handleChange, handleChangeList }: any) => {
	const { domain: identifikatorTypeOptions, loading: loadingIdentifikatorType } =
		useTenorDomain('IdentifikatorType')
	const { domain: kjoennOptions, loading: loadingKjoenn } = useTenorDomain('Kjoenn')
	const { domain: personstatusOptions, loading: loadingPersonstatus } =
		useTenorDomain('Personstatus')
	const { domain: sivilstatusOptions, loading: loadingSivilstatus } = useTenorDomain('Sivilstatus')
	const { domain: utenlandskPersonIdentifikasjonOptions, loading: loadingUtenlandsid } =
		useTenorDomain('UtenlandskPersonIdentifikasjon')
	const { domain: identitetsgrunnlagStatusOptions, loading: loadingIdentgrunnlag } = useTenorDomain(
		'IdentitetsgrunnlagStatus',
	)
	const { domain: adressebeskyttelseOptions, loading: loadingAdressebeskyttelse } =
		useTenorDomain('Adressebeskyttelse')

	return (
		<SoekKategori>
			<FormTextInput
				name="identifikator"
				label="Fødselsnummer / D-nummer"
				// @ts-ignore
				onBlur={(val: any) =>
					handleChange(
						val?.target?.value || null,
						'identifikator',
						`Fødselsnummer/D-nummer: ${val?.target?.value}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="identifikatorType"
				options={createOptions(identifikatorTypeOptions?.data, true)}
				label="Identifikatortype"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'identifikatorType',
						`Identifikatortype: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingIdentifikatorType}
			/>
			<FormDatepicker
				name="foedselsdato.fraOgMed"
				label="Fødselsdato f.o.m."
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val || null,
						'foedselsdato.fraOgMed',
						`Fødselsdato f.o.m.: ${formatDate(val)}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="foedselsdato.tilOgMed"
				label="Fødselsdato t.o.m."
				onChange={(val: SyntheticEvent) =>
					handleChange(
						val || null,
						'foedselsdato.tilOgMed',
						`Fødselsdato t.o.m.: ${formatDate(val)}`,
					)
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="doedsdato.fraOgMed"
				label="Dødsdato f.o.m."
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'doedsdato.fraOgMed', `Dødsdato f.o.m.: ${formatDate(val)}`)
				}
				visHvisAvhuket={false}
			/>
			<FormDatepicker
				name="doedsdato.tilOgMed"
				label="Dødsdato t.o.m."
				onChange={(val: SyntheticEvent) =>
					handleChange(val || null, 'doedsdato.tilOgMed', `Dødsdato t.o.m.: ${formatDate(val)}`)
				}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="kjoenn"
				options={createOptions(kjoennOptions?.data)}
				label="Kjønn"
				onChange={(val: any) =>
					handleChange(val?.value || null, 'kjoenn', `Kjønn: ${codeToNorskLabel(val?.value)}`)
				}
				isLoading={loadingKjoenn}
			/>
			<FormSelect
				name="personstatus"
				options={createOptions(personstatusOptions?.data)}
				label="Personstatus"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'personstatus',
						`Personstatus: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingPersonstatus}
			/>
			<FormSelect
				name="sivilstand"
				options={createOptions(sivilstatusOptions?.data)}
				label="Sivilstand"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'sivilstand',
						`Sivilstand: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingSivilstatus}
			/>
			<FormSelect
				name="identitetsgrunnlagStatus"
				options={createOptions(identitetsgrunnlagStatusOptions?.data)}
				label="Identitetsgrunnlagsstatus"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'identitetsgrunnlagStatus',
						`Identitetsgrunnlagsstatus: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingIdentgrunnlag}
			/>
			<FormSelect
				name="adressebeskyttelse"
				options={createOptions(adressebeskyttelseOptions?.data)}
				label="Adressebeskyttelse"
				onChange={(val: any) =>
					handleChange(
						val?.value || null,
						'adressebeskyttelse',
						`Adressebeskyttelse: ${codeToNorskLabel(val?.value)}`,
					)
				}
				isLoading={loadingAdressebeskyttelse}
			/>
			<FormSelect
				name="harFalskIdentitet"
				options={Options('boolean')}
				label="Har falsk identitet"
				onChange={(val: any) =>
					handleChange(
						val?.value,
						'harFalskIdentitet',
						`Har falsk identitet: ${oversettBoolean(val?.value)}`,
					)
				}
			/>
			<div className="flexbox--full-width">
				<FormSelect
					name="utenlandskPersonIdentifikasjon"
					options={createOptions(utenlandskPersonIdentifikasjonOptions?.data)}
					isMulti={true}
					size="grow"
					label="Utenlandsk identifikasjonsnummertype"
					onChange={(val: SyntheticEvent) => {
						handleChangeList(
							val?.map((item: any) => item.value) || null,
							'utenlandskPersonIdentifikasjon',
							'Utenlandsk ident.nr.type',
						)
					}}
					isLoading={loadingUtenlandsid}
				/>
			</div>
			<FormCheckbox
				name="harLegitimasjonsdokument"
				data-testid={TestComponentSelectors.CHECKBOX_TENORSOEK}
				label="Har legitimasjonsdokument"
				onChange={(val: any) =>
					handleChange(
						val?.target?.checked || undefined,
						'harLegitimasjonsdokument',
						'Har legitimasjonsdokument',
					)
				}
			/>
		</SoekKategori>
	)
}
