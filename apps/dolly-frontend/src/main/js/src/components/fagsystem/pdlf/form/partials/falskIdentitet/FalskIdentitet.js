import React, { useState } from 'react'
import _get from 'lodash/get'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn, setValue } from '../utils'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import _has from 'lodash/has'

export const FalskIdentitet = ({ formikBag }) => {
	// const rettIdentTyper = {
	// 	UKJENT: 'rettIdentitetErUkjent',
	// 	ENTYDIG: 'rettIdentitetVedIdentifikasjonsnummer',
	// 	OMTRENTLIG: 'rettIdentitetVedOpplysninger',
	// }

	// const [rettIdent, setRettIdent] = useState(null)

	// const getRettIdent = ()

	// const initialFalskIdent = {
	//
	// }

	// console.log('formikBag', formikBag)
	// const falskIdPath = 'pdldata.person.falskIdentitet'
	// const falskIdObj = formikBag.values.pdldata.person.falskIdentitet
	// console.log('falskIdObj', falskIdObj)

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)
	const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	const settIdentitetType = (e, path) => {
		if (!e) {
			formikBag.setFieldValue(path, { erFalsk: true })
			// setRettIdent(null)
		} else if (e.value === 'UKJENT') {
			formikBag.setFieldValue(path, { erFalsk: true, rettIdentitetErUkjent: true })
			// setRettIdent('UKJENT')
			// formikBag.setFieldValue(falskIdPath, { identitetType: e.value, rettIdentitetErUkjent: true })
		} else if (e.value === 'ENTYDIG') {
			formikBag.setFieldValue(path, {
				erFalsk: true,
				rettIdentitetVedIdentifikasjonsnummer: '',
			})
			// setRettIdent('ENTYDIG')
		} else if (e.value === 'OMTRENTLIG') {
			formikBag.setFieldValue(path, {
				erFalsk: true,
				rettIdentitetVedOpplysninger: {
					foedselsdato: '',
					kjoenn: '',
					personnavn: { fornavn: '', mellomnavn: '', etternavn: '' },
					statsborgerskap: [],
				},
			})
			// setRettIdent('OMTRENTLIG')
		}
		return e.value
		// TODO funker ikke på clear??
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.falskIdentitet"
			header="Falsk identitet"
			newEntry={{}}
		>
			{(path, idx) => {
				const identType = _has(formikBag.values, `${path}.rettIdentitetErUkjent`)
					? 'UKJENT'
					: _has(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)
					? 'ENTYDIG'
					: _has(formikBag.values, `${path}.rettIdentitetVedOpplysninger`)
					? 'OMTRENTLIG'
					: null

				return (
					<div key={idx}>
						<FormikSelect
							name={`${path}.identitetType`} // TODO hva gjør jeg med denne?
							label="Opplysninger om rett identitet"
							options={Options('identitetType')}
							value={identType}
							// value={falskIdObj.identitetType}
							onChange={(e) => settIdentitetType(e, path)}
							// isClearable={false}
							size="large"
						/>

						{identType === 'ENTYDIG' && (
							<DollySelect
								name={`${path}.rettIdentitetVedIdentifikasjonsnummer`}
								label="Navn og identifikasjonsnummer"
								size="large"
								options={navnOgFnrOptions}
								isLoading={dollyGruppeInfo.loading}
								onChange={(id) =>
									setValue(
										id,
										`${path}.rettIdentitetVedIdentifikasjonsnummer`,
										formikBag.setFieldValue
									)
								}
								value={_get(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)}
								isClearable={false}
							/>
						)}
						{identType === 'OMTRENTLIG' && (
							<div className="flexbox--flex-wrap">
								<DollySelect
									name={`${path}.rettIdentitetVedOpplysninger.personnavn.fornavn`}
									label="Navn"
									options={navnOptions}
									size="large"
									isLoading={navnInfo.loading}
									onChange={(navn) =>
										setNavn(
											navn,
											`${path}.rettIdentitetVedOpplysninger.personnavn`,
											formikBag.setFieldValue
										)
									}
									value={_get(formikBag.values, `${path}.personnavn.fornavn`)}
									isClearable={false}
									placeholder={getPlaceholder(
										formikBag.values,
										`${path}.rettIdentitetVedOpplysninger.personnavn`
									)}
								/>
								<FormikDatepicker
									name={`${path}.rettIdentitetVedOpplysninger.foedselsdato`}
									label="Fødselsdato"
								/>
								<FormikSelect
									name={`${path}.rettIdentitetVedOpplysninger.kjoenn`}
									label="Kjønn"
									options={Options('kjoennFalskIdentitet')}
									isClearable={false}
								/>
								<FormikSelect
									name={`${path}.rettIdentitetVedOpplysninger.statsborgerskap`}
									label="Statsborgerskap"
									kodeverk={AdresseKodeverk.StatsborgerskapLand}
									isClearable={false}
									isMulti={true}
									fastfield={false}
									size="large"
								/>
							</div>
						)}
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
