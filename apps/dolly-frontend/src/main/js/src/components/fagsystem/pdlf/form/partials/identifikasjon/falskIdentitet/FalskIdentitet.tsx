import * as _ from 'lodash-es'
import { AdresseKodeverk } from '@/config/kodeverk'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn } from '../../utils'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialFalskIdentitetValues } from '@/components/fagsystem/pdlf/form/initialValues'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export const FalskIdentitet = ({ formMethods }) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

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
		formMethods.trigger(path)
		return e.value
	}

	return (
		<FormDollyFieldArray
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
					kilde: formMethods.watch(`${path}.kilde`),
					master: formMethods.watch(`${path}.master`),
				}

				const identTyper = Options('identitetType').filter(
					(type) => type.value !== 'ENTYDIG' || opts?.antall == 1,
				)

				return (
					<>
						<div className="flexbox--flex-wrap" key={idx}>
							<FormSelect
								name={`${path}.identitetType`}
								label="Opplysninger om rett identitet"
								options={identTyper}
								value={identType()}
								onChange={(e) => settIdentitetType(e, path, advancedValues)}
								size="large"
								info={
									opts?.antall > 1 &&
									'"Ved identifikasjonsnummer" er tilgjengelig for individ, ikke for gruppe'
								}
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
										value={formMethods.watch(
											`${path}.rettIdentitetVedOpplysninger.personnavn.fornavn`,
										)}
									/>
									<FormDatepicker
										name={`${path}.rettIdentitetVedOpplysninger.foedselsdato`}
										label="Fødselsdato"
										maxDate={new Date()}
									/>
									<FormSelect
										name={`${path}.rettIdentitetVedOpplysninger.kjoenn`}
										label="Kjønn"
										options={Options('kjoenn')}
									/>
									<FormSelect
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
		</FormDollyFieldArray>
	)
}
