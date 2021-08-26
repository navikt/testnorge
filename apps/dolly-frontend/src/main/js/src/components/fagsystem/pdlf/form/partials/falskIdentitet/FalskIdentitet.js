import React from 'react'
import _get from 'lodash/get'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setValue, setNavn } from '../utils'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const FalskIdentitet = ({ formikBag }) => {
	const falskIdPath = 'pdlforvalter.falskIdentitet.rettIdentitet'
	const falskIdObj = formikBag.values.pdlforvalter.falskIdentitet.rettIdentitet

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)
	const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	const settIdentitetType = e => {
		if (e.value === 'UKJENT') {
			formikBag.setFieldValue(falskIdPath, { identitetType: e.value, rettIdentitetErUkjent: true })
		} else if (e.value === 'ENTYDIG') {
			formikBag.setFieldValue(falskIdPath, {
				identitetType: e.value,
				rettIdentitetVedIdentifikasjonsnummer: ''
			})
		} else {
			formikBag.setFieldValue(falskIdPath, {
				identitetType: e.value,
				foedselsdato: '',
				kjoenn: '',
				personnavn: { fornavn: '', mellomnavn: '', etternavn: '' },
				statsborgerskap: ''
			})
		}
		return e.value
	}

	return (
		<div>
			<FormikSelect
				name={`${falskIdPath}.identitetType`}
				label="Opplysninger om rett identitet"
				options={Options('identitetType')}
				value={falskIdObj.identitetType}
				onChange={settIdentitetType}
				isClearable={false}
				size="medium"
			/>

			{falskIdObj.identitetType === 'ENTYDIG' && (
				<DollySelect
					name={`${falskIdPath}.rettIdentitetVedIdentifikasjonsnummer`}
					label="Navn og identifikasjonsnummer"
					size="large"
					options={navnOgFnrOptions}
					isLoading={dollyGruppeInfo.loading}
					onChange={id =>
						setValue(
							id,
							`${falskIdPath}.rettIdentitetVedIdentifikasjonsnummer`,
							formikBag.setFieldValue
						)
					}
					value={_get(formikBag.values, `${falskIdPath}.rettIdentitetVedIdentifikasjonsnummer`)}
					isClearable={false}
				/>
			)}
			{falskIdObj.identitetType === 'OMTRENTLIG' && (
				<div className="flexbox--flex-wrap">
					<DollySelect
						name={`${falskIdPath}.personnavn.fornavn`}
						label="Navn"
						options={navnOptions}
						size="large"
						isLoading={navnInfo.loading}
						onChange={navn => setNavn(navn, `${falskIdPath}.personnavn`, formikBag.setFieldValue)}
						value={_get(formikBag.values, `${falskIdPath}.personnavn.fornavn`)}
						isClearable={false}
						placeholder={getPlaceholder(formikBag.values, `${falskIdPath}.personnavn`)}
					/>
					<FormikDatepicker name={`${falskIdPath}.foedselsdato`} label="Fødselsdato" />
					<FormikSelect
						name={`${falskIdPath}.kjoenn`}
						label="Kjønn"
						options={Options('kjoennFalskIdentitet')}
						isClearable={false}
					/>
					<FormikSelect
						name={`${falskIdPath}.statsborgerskap`}
						label="Statsborgerskap"
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
						isClearable={false}
						isMulti={true}
						size="large"
					/>
				</div>
			)}
		</div>
	)
}
