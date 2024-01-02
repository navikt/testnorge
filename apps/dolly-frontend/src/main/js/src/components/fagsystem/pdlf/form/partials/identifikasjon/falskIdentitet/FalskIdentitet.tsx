import * as _ from 'lodash'
import { AdresseKodeverk } from '@/config/kodeverk'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn } from '../../utils'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialFalskIdentitetValues } from '@/components/fagsystem/pdlf/form/initialValues'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'

export const FalskIdentitet = ({ formMethods }) => {
	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	const settIdentitetType = (e, path, advancedValues) => {
		if (!e) {
			formMethods.setValue(path, advancedValues)
			return null
		} else if (e.value === 'UKJENT') {
			formMethods.setValue(path, { rettIdentitetErUkjent: true, ...advancedValues })
		} else if (e.value === 'ENTYDIG') {
			formMethods.setValue(path, {
				rettIdentitetVedIdentifikasjonsnummer: null,
				...advancedValues,
			})
		} else if (e.value === 'OMTRENTLIG') {
			formMethods.setValue(path, {
				rettIdentitetVedOpplysninger: {
					foedselsdato: null,
					kjoenn: null,
					personnavn: null,
					statsborgerskap: [],
				},
				...advancedValues,
			})
		}
		formMethods.trigger()
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
					if (_.has(formMethods.getValues(), `${path}.rettIdentitetErUkjent`)) {
						return 'UKJENT'
					} else if (
						_.has(formMethods.getValues(), `${path}.rettIdentitetVedIdentifikasjonsnummer`)
					) {
						return 'ENTYDIG'
					}
					return _.has(formMethods.getValues(), `${path}.rettIdentitetVedOpplysninger`)
						? 'OMTRENTLIG'
						: null
				}

				const advancedValues = {
					erFalsk: true,
					kilde: _.get(formMethods.getValues(), `${path}.kilde`),
					master: _.get(formMethods.getValues(), `${path}.master`),
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
									formMethods={formMethods}
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
											formMethods.getValues(),
											`${path}.rettIdentitetVedOpplysninger.personnavn`,
										)}
										isLoading={loading}
										onChange={(navn) =>
											setNavn(
												navn,
												`${path}.rettIdentitetVedOpplysninger.personnavn`,
												formMethods.setValue,
											)
										}
										value={_.get(
											formMethods.getValues(),
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
