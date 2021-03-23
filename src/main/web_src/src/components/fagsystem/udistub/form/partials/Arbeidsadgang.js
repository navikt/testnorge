import React from 'react'
import _get from 'lodash/get'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Textarea } from 'nav-frontend-skjema'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Arbeidsadgang = ({ formikBag }) => {
	const harArbeidsAdgang = _get(formikBag.values, 'udistub.arbeidsadgang.harArbeidsAdgang')

	const endreArbeidsadgang = v => {
		formikBag.setFieldValue('udistub.arbeidsadgang.harArbeidsAdgang', v.value)
		if (v.value !== 'JA') {
			formikBag.setFieldValue('udistub.arbeidsadgang.arbeidsOmfang', null)
			formikBag.setFieldValue('udistub.arbeidsadgang.periode', {
				fra: null,
				til: null
			})
			formikBag.setFieldValue('udistub.arbeidsadgang.typeArbeidsadgang', null)
		}
	}

	const forklaring = _get(formikBag.values, 'udistub.arbeidsadgang.forklaring')

	const endreForklaring = text => {
		formikBag.setFieldValue('udistub.arbeidsadgang.forklaring', text === '' ? null : text)
	}

	const MAX_LENGTH = 4000

	return (
		<>
			<Kategori title="Arbeidsadgang" vis="udistub.arbeidsadgang">
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
			</Kategori>
			<Kategori title="Innhent vedtakshjemmel" vis="udistub.arbeidsadgang.hjemmel">
				<FormikTextInput name="udistub.arbeidsadgang.hjemmel" label="Hjemmel" size="xxlarge" />
				<div className="flexbox--full-width">
					<Textarea
						value={forklaring ? forklaring : ''}
						name="udistub.arbeidsadgang.forklaring"
						label="Forklaring"
						placeholder="Skriv inn forklaring"
						maxLength={MAX_LENGTH}
						onChange={event => endreForklaring(event.target.value)}
						feil={
							forklaring && forklaring.length > MAX_LENGTH
								? { feilmelding: 'Forklaring kan ikke være lenger enn 4000 tegn' }
								: null
						}
					/>
				</div>
			</Kategori>
		</>
	)
}
