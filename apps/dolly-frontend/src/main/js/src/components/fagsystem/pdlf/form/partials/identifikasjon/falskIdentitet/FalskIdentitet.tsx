import * as _ from 'lodash-es'
import { AdresseKodeverk } from '@/config/kodeverk'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn } from '../../utils'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialFalskIdentitetValues } from '@/components/fagsystem/pdlf/form/initialValues'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'

export const FalskIdentitet = ({ formikBag }) => {
	const { navnInfo } = useGenererNavn()
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
			newEntry={initialFalskIdentitetValues}
			canBeEmpty={false}
		>
			{(path, idx) => {
				const identType = () => {
					if (_.has(formikBag.values, `${path}.rettIdentitetErUkjent`)) {
						return 'UKJENT'
					} else if (_.has(formikBag.values, `${path}.rettIdentitetVedIdentifikasjonsnummer`)) {
						return 'ENTYDIG'
					}
					return _.has(formikBag.values, `${path}.rettIdentitetVedOpplysninger`)
						? 'OMTRENTLIG'
						: null
				}

				const advancedValues = {
					erFalsk: true,
					kilde: _.get(formikBag.values, `${path}.kilde`),
					master: _.get(formikBag.values, `${path}.master`),
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
								<PdlEksisterendePerson
									eksisterendePersonPath={`${path}.rettIdentitetVedIdentifikasjonsnummer`}
									label="Eksisterende identifikasjonsnummer"
									formikBag={formikBag}
								/>
							)}
							{identType() === 'OMTRENTLIG' && (
								<>
									<DollySelect
										name={`${path}.rettIdentitetVedOpplysninger.personnavn.fornavn`}
										label="Navn"
										options={navnOptions}
										size="xlarge"
										placeholder={getPlaceholder(
											formikBag.values,
											`${path}.rettIdentitetVedOpplysninger.personnavn`,
										)}
										isLoading={navnInfo.loading}
										onChange={(navn) =>
											setNavn(
												navn,
												`${path}.rettIdentitetVedOpplysninger.personnavn`,
												formikBag.setFieldValue,
											)
										}
										value={_.get(
											formikBag.values,
											`${path}.rettIdentitetVedOpplysninger.personnavn.fornavn`,
										)}
									/>
									<FormikDatepicker
										name={`${path}.rettIdentitetVedOpplysninger.foedselsdato`}
										label="Fødselsdato"
										maxDate={new Date()}
									/>
									<FormikSelect
										name={`${path}.rettIdentitetVedOpplysninger.kjoenn`}
										label="Kjønn"
										options={Options('kjoenn')}
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
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
