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
import { FalskIdentitetToggle } from '~/components/fagsystem/pdlf/form/partials/falskIdentitet/FalskIdentitetToggle'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'

const initialValues = {
	erFalsk: true,
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}

export const FalskIdentitet = ({ formikBag }) => {
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)

	const settIdentitetType = (e, path, advancedValues) => {
		if (!e) {
			formikBag.setFieldValue(path, advancedValues)
			return null
		} else if (e.value === 'UKJENT') {
			formikBag.setFieldValue(path, { rettIdentitetErUkjent: true, ...advancedValues })
		} else if (e.value === 'ENTYDIG') {
			formikBag.setFieldValue(path, {
				rettIdentitetVedIdentifikasjonsnummer: null,
				...advancedValues,
			})
		} else if (e.value === 'OMTRENTLIG') {
			formikBag.setFieldValue(path, {
				rettIdentitetVedOpplysninger: {
					foedselsdato: null,
					kjoenn: null,
					personnavn: null,
					statsborgerskap: [],
				},
				...advancedValues,
			})
		}
		return e.value
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.falskIdentitet"
			header="Falsk identitet"
			newEntry={initialValues}
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

				const advancedValues = {
					erFalsk: true,
					kilde: _get(formikBag.values, `${path}.kilde`),
					master: _get(formikBag.values, `${path}.master`),
					gjeldende: _get(formikBag.values, `${path}.gjeldende`),
				}

				return (
					<>
						<div className="flexbox--flex-wrap" key={idx}>
							<FormikSelect
								name={`${path}.identitetType`}
								label="Opplysninger om rett identitet"
								options={Options('identitetType')}
								value={identType()}
								onChange={(e) => settIdentitetType(e, path, advancedValues)}
								size="large"
							/>

							{identType() === 'ENTYDIG' && (
								<FalskIdentitetToggle formikBag={formikBag} path={path} />
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
						<AvansertForm path={path} />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
