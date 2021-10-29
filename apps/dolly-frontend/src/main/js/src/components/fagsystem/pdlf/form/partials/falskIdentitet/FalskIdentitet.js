import React from 'react'
import _get from 'lodash/get'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { setNavn } from '../utils'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import _has from 'lodash/has'
import Hjelpetekst from '~/components/hjelpetekst'

export const FalskIdentitet = ({ formikBag }) => {
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)
	const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	const settIdentitetType = (e, path) => {
		if (!e) {
			formikBag.setFieldValue(path, { erFalsk: true })
			return null
		} else if (e.value === 'UKJENT') {
			formikBag.setFieldValue(path, { erFalsk: true, rettIdentitetErUkjent: true })
		} else if (e.value === 'ENTYDIG') {
			formikBag.setFieldValue(path, {
				erFalsk: true,
				rettIdentitetVedIdentifikasjonsnummer: null,
			})
		} else if (e.value === 'OMTRENTLIG') {
			formikBag.setFieldValue(path, {
				erFalsk: true,
				rettIdentitetVedOpplysninger: {
					foedselsdato: null,
					kjoenn: null,
					personnavn: null,
					statsborgerskap: [],
				},
			})
		}
		return e.value
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.falskIdentitet"
			header="Falsk identitet"
			newEntry={{ erFalsk: true }}
			canBeEmpty={false}
		>
			{(path, idx) => {
				const identType = () => {
					if (_has(formikBag.values, `${path}.rettIdentitetErUkjent`)) {
						return 'UKJENT'
					} else if (_has(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)) {
						return 'ENTYDIG'
					}
					return _has(formikBag.values, `${path}.rettIdentitetVedOpplysninger`)
						? 'OMTRENTLIG'
						: null
				}

				return (
					<div className="flexbox--flex-wrap" key={idx}>
						<FormikSelect
							name={`${path}.identitetType`}
							label="Opplysninger om rett identitet"
							options={Options('identitetType')}
							value={identType()}
							onChange={(e) => settIdentitetType(e, path)}
							size="large"
						/>

						{identType() === 'ENTYDIG' && (
							<div className="flexbox--align-center">
								<DollySelect
									name={`${path}.rettIdentitetVedIdentifikasjonsnummer`}
									label="Navn og identifikasjonsnummer"
									size="large"
									options={navnOgFnrOptions}
									isLoading={dollyGruppeInfo.loading}
									onChange={(id) =>
										formikBag.setFieldValue(
											`${path}.rettIdentitetVedIdentifikasjonsnummer`,
											id ? id.value : null
										)
									}
									value={_get(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)}
									disabled={true}
								/>
								<Hjelpetekst hjelpetekstFor={`${path}.rettIdentitetVedIdentifikasjonsnummer`}>
									{
										'For øyeblikket er det ikke mulig å velge eksisterende ident - ved bestilling vil det automatisk opprettes en ny ident.'
									}
								</Hjelpetekst>
							</div>
						)}
						{identType() === 'OMTRENTLIG' && (
							<>
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
									value={_get(
										formikBag.values,
										`${path}.rettIdentitetVedOpplysninger.personnavn.fornavn`
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
							</>
						)}
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
