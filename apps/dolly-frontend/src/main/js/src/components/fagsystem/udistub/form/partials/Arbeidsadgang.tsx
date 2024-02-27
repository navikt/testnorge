import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Textarea } from '@navikt/ds-react'
import styled from 'styled-components'
import React from 'react'

export const Arbeidsadgang = ({ formMethods }) => {
	const StyledTextArea = styled(Textarea)`
		margin-bottom: 1rem;

		textarea {
			font-size: 1em;
		}

		&& {
			label {
				font-size: 0.75em;
				text-transform: uppercase;
				font-weight: 400;
				margin-bottom: -8px;
			}
		}
	`

	const harArbeidsAdgang = formMethods.watch('udistub.arbeidsadgang.harArbeidsAdgang')

	const endreArbeidsadgang = (v) => {
		formMethods.setValue('udistub.arbeidsadgang.harArbeidsAdgang', v.value)
		if (v.value !== 'JA') {
			formMethods.setValue('udistub.arbeidsadgang.arbeidsOmfang', null)
			formMethods.setValue('udistub.arbeidsadgang.periode', {
				fra: null,
				til: null,
			})
			formMethods.setValue('udistub.arbeidsadgang.typeArbeidsadgang', null)
		}
		formMethods.trigger('udistub.arbeidsadgang')
	}

	const forklaring = formMethods.watch('udistub.arbeidsadgang.forklaring')

	const endreForklaring = (text) => {
		formMethods.setValue('udistub.arbeidsadgang.forklaring', text === '' ? null : text)
	}

	const MAX_LENGTH = 4000

	return (
		<>
			<Kategori title="Arbeidsadgang" vis="udistub.arbeidsadgang">
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name="udistub.arbeidsadgang.harArbeidsAdgang"
						label="Har arbeidsadgang"
						options={Options('jaNeiUavklart')}
						value={harArbeidsAdgang}
						onChange={endreArbeidsadgang}
						isClearable={false}
					/>
					{harArbeidsAdgang === 'JA' && (
						<React.Fragment>
							<FormikSelect
								name="udistub.arbeidsadgang.typeArbeidsadgang"
								label="Type arbeidsadgang"
								options={Options('typeArbeidsadgang')}
								size="xxlarge"
							/>
							<FormikSelect
								name="udistub.arbeidsadgang.arbeidsOmfang"
								label="Arbeidsomfang"
								options={Options('arbeidsOmfang')}
								size="medium"
							/>
							<FormikDatepicker
								name="udistub.arbeidsadgang.periode.fra"
								label="Arbeidsadgang fra dato"
							/>
							<FormikDatepicker
								name="udistub.arbeidsadgang.periode.til"
								label="Arbeidsadgang til dato"
							/>
						</React.Fragment>
					)}
				</div>
			</Kategori>
			<Kategori title="Innhent vedtakshjemmel" vis="udistub.arbeidsadgang.hjemmel">
				<FormikTextInput name="udistub.arbeidsadgang.hjemmel" label="Hjemmel" size="xxlarge" />
				<div className="flexbox--full-width">
					<StyledTextArea
						defaultValue={forklaring ? forklaring : ''}
						size={'small'}
						key="udistub.arbeidsadgang.forklaring"
						label="Forklaring"
						placeholder="Skriv inn forklaring"
						maxLength={MAX_LENGTH}
						onBlur={(event) => endreForklaring(event.target.value)}
					/>
				</div>
			</Kategori>
		</>
	)
}
