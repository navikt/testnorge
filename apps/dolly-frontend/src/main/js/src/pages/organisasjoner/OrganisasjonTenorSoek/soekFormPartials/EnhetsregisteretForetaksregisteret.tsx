import { SoekKategori } from '@/components/ui/soekForm/SoekFormWrapper'
import React from 'react'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorOrganisasjonDomain } from '@/utils/hooks/useTenorSoek'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Option } from '@/service/SelectOptionsOppslag'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const EnhetsregisteretForetaksregisteret = ({ handleChange }: any) => {
	const { domain: orgformOptions } = useTenorOrganisasjonDomain('Organisasjonsform')
	const { domain: enhetStatusOptions } = useTenorOrganisasjonDomain('EnhetStatus')
	return (
		<SoekKategori>
			<FormTextInput
				name="organisasjonsnummer"
				label="Organisasjonsnummer"
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'organisasjonsnummer')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="organisasjonsform.kode"
				options={createOptions(orgformOptions?.data)}
				label="Organisasjonsform"
				size={'xlarge'}
				onChange={(val: any) => handleChange(val?.value || null, 'organisasjonsform.kode')}
			/>

			<FormSelect
				name="forretningsadresse.kommunenummer"
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size={'large'}
				onChange={(val: Option) =>
					handleChange(val?.value || null, 'forretningsadresse.kommunenummer')
				}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="enhetStatuser.kode"
				options={createOptions(enhetStatusOptions?.data)}
				label="Enhet status"
				onChange={(val: any) => handleChange(val?.value || null, 'enhetStatuser.kode')}
			/>
			<FormTextInput
				name="naeringBeskrivelse"
				label="Næringsbeskrivelse"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'naeringBeskrivelse')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="naeringKode"
				label="Næringskode"
				placeholder={'00.000'}
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'naeringKode')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="antallUnderenheter"
				label="Antall underenheter"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'antallUnderenheter')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="antallAnsatte.fraOgMed"
				label="Minimum antall ansatte"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'antallAnsatte.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="antallAnsatte.tilOgMed"
				label="Maks antall ansatte"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'antallAnsatte.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="harUtenlandskForretningsadresse"
				label="Utenlandsk forr. adresse"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'harUtenlandskForretningsadresse')}
			/>
			<FormSelect
				name="harUtenlandskPostadresse"
				label="Utenlandsk postadresse"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'harUtenlandskPostadresse')}
			/>
			<FormSelect
				name="slettetIEnhetsregisteret"
				label="Slettet i enhetsregisteret"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'slettetIEnhetsregisteret')}
			/>
			<FormSelect
				name="registrertIMvaregisteret"
				label="Registrert i MVA registeret"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'registrertIMvaregisteret')}
			/>
			<FormSelect
				name="registrertIForetaksregisteret"
				label="Registrert i foretaksregisteret"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'registrertIForetaksregisteret')}
			/>
			<FormSelect
				name="registrertIFrivillighetsregisteret"
				label="Reg. i frivillighetsregisteret"
				options={Options('boolean')}
				onChange={(val: Option) => handleChange(val?.value, 'registrertIFrivillighetsregisteret')}
			/>
			<div className={'flexbox'} style={{ flexFlow: 'wrap', width: '-webkit-fill-available' }}>
				<FormCheckbox
					name="erUnderenhet.hovedenhet"
					label="Er underenhet"
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'erUnderenhet.hovedenhet')
					}
				/>
				<FormCheckbox
					name="harUnderenheter"
					label="Har underenheter"
					data-testid={TestComponentSelectors.CHECKBOX_ORGANISASJONER_TENORSOEK}
					onChange={(val: any) =>
						handleChange(val?.target?.checked || undefined, 'harUnderenheter')
					}
				/>
			</div>
		</SoekKategori>
	)
}
