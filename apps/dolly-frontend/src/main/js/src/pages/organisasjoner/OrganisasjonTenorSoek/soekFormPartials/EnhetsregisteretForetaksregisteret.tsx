import { SoekKategori } from '@/components/ui/soekForm/SoekForm'
import React from 'react'
import { createOptions } from '@/pages/tenorSoek/utils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useTenorOrganisasjonDomain } from '@/utils/hooks/useTenorSoek'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { AdresseKodeverk } from '@/config/kodeverk'
import { Option } from '@/service/SelectOptionsOppslag'

export const EnhetsregisteretForetaksregisteret = ({ handleChange, handleChangeList }: any) => {
	const { domain: orgformOptions } = useTenorOrganisasjonDomain('Organisasjonsform')
	const { domain: enhetStatusOptions } = useTenorOrganisasjonDomain('EnhetStatus')
	return (
		<SoekKategori>
			<FormTextInput
				name="organisasjonsnummer"
				label="Organisasjonsnummer"
				// @ts-ignore
				onKeyPress={(val: any) => handleChange(val?.target?.value || null, 'organisasjonsnummer')}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="organisasjonsform.kode"
				options={createOptions(orgformOptions?.data, true)}
				label="Organisasjonsform"
				size={'xlarge'}
				onChange={(val: any) => handleChange(val?.value || null, 'organisasjonsform.kode')}
			/>

			<FormSelect
				name="forretningsadresse.kommunenummer"
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size={'small'}
				onChange={(val: Option) =>
					handleChange(val?.value || null, 'forretningsadresse.kommunenummer')
				}
				visHvisAvhuket={false}
			/>
			<FormSelect
				name="enhetStatuser.kode"
				options={createOptions(enhetStatusOptions?.data, true)}
				label="Enhet status"
				onChange={(val: any) => handleChange(val?.value || null, 'enhetStatuser.kode')}
			/>
			<FormTextInput
				name="naeringBeskrivelse"
				label="Næring beskrivelse"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'naeringBeskrivelse')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="antallAnsatte.fraOgMed"
				label="Antall ansatte fra og med"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'antallAnsatte.fraOgMed')}
				visHvisAvhuket={false}
			/>
			<FormTextInput
				name="antallAnsatte.tilOgMed"
				label="Antall ansatte til og med"
				// @ts-ignore
				onBlur={(val: any) => handleChange(val?.target?.value || null, 'antallAnsatte.tilOgMed')}
				visHvisAvhuket={false}
			/>
			<FormCheckbox
				name="harUtenlandskForretningsadresse"
				label="Har utenlandsk forretningsadresse"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harUtenlandskForretningsadresse')
				}
			/>
			<FormCheckbox
				name="harUtenlandskPostadresse"
				label="Har utenlandsk postadresse"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'harUtenlandskPostadresse')
				}
			/>
			<FormCheckbox
				name="slettetIEnhetsregisteret"
				label="Slettet i Enhetsregisteret"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'slettetIEnhetsregisteret')
				}
			/>
			<FormCheckbox
				name="registrertIMvaregisteret"
				label="Registrert i MVA registeret"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'registrertIMvaregisteret')
				}
			/>
			<FormCheckbox
				name="registrertIForetaksregisteret"
				label="Registrert i Foretaksregisteret"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'registrertIForetaksregisteret')
				}
			/>
			<FormCheckbox
				name="registrertIFrivillighetsregisteret"
				label="Registrert i Frivillighetsregisteret"
				onChange={(val: any) =>
					handleChange(val?.target?.checked || undefined, 'registrertIFrivillighetsregisteret')
				}
			/>
		</SoekKategori>
	)
}
