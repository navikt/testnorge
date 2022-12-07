import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialBarn,
	initialForelder,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { BarnRelasjon } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/BarnRelasjon'
import { TypeAnsvarlig } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlPersonUtenIdentifikator } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { ToggleGroup } from '@navikt/ds-react'

interface ForelderForm {
	formikBag: FormikProps<{}>
}

type Target = {
	label: string
	value: string
}

const RELASJON_BARN = 'Barn'
const RELASJON_FORELDER = 'Forelder'

export const ForelderBarnRelasjon = ({ formikBag }: ForelderForm) => {
	const relatertPerson = 'relatertPerson'
	const nyRelatertPerson = 'nyRelatertPerson'
	const relatertPersonUtenFolkeregisteridentifikator =
		'relatertPersonUtenFolkeregisteridentifikator'

	const handleChangeTypeForelderBarn = (target: Target, path: string) => {
		const forelderBarnRelasjon = _.get(formikBag.values, path)
		const forelderBarnClone = _.cloneDeep(forelderBarnRelasjon)

		_.set(forelderBarnClone, 'typeForelderBarn', target?.value || null)
		if (!target) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.EKSISTERENDE) {
			_.set(forelderBarnClone, relatertPerson, null)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.UTEN_ID) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, initialPdlBiPerson)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.NY) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, initialPdlPerson)
		}

		formikBag.setFieldValue(path, forelderBarnClone)
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={initialBarn}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => {
				const erBarn = _.get(formikBag.values, path)?.partnerErIkkeForelder !== undefined
				const forelderBarnType = _.get(formikBag.values, `${path}.typeForelderBarn`)

				return (
					<div className="flexbox--flex-wrap">
						<div className="toggle--wrapper">
							<ToggleGroup
								onChange={(value: string) => {
									formikBag.setFieldValue(
										path,
										value === RELASJON_BARN ? initialBarn : initialForelder
									)
								}}
								size={'small'}
								defaultValue={RELASJON_BARN}
							>
								<ToggleGroup.Item value={RELASJON_BARN}>{RELASJON_BARN}</ToggleGroup.Item>
								<ToggleGroup.Item value={RELASJON_FORELDER}>{RELASJON_FORELDER}</ToggleGroup.Item>
							</ToggleGroup>
						</div>
						<div className="flexbox--flex-wrap">
							{erBarn && <BarnRelasjon formikBag={formikBag} path={path} />}
							{!erBarn && (
								<>
									<FormikSelect
										name={`${path}.relatertPersonsRolle`}
										label="Foreldretype"
										options={Options('foreldreTypePDL')}
										isClearable={false}
									/>
									<FormikCheckbox
										name={`${path}.borIkkeSammen`}
										label="Bor ikke sammen"
										checkboxMargin
									/>
								</>
							)}
							<FormikSelect
								name={`${path}.typeForelderBarn`}
								label={erBarn ? 'Type barn' : 'Type forelder'}
								options={Options('typeAnsvarlig')}
								onChange={(target: Target) => handleChangeTypeForelderBarn(target, path)}
								size="medium"
							/>
						</div>

						{forelderBarnType === TypeAnsvarlig.EKSISTERENDE && (
							<PdlEksisterendePerson
								eksisterendePersonPath={`${path}.relatertPerson`}
								label={erBarn ? RELASJON_BARN.toUpperCase() : RELASJON_FORELDER.toUpperCase()}
								formikBag={formikBag}
								idx={idx}
							/>
						)}

						{forelderBarnType === TypeAnsvarlig.UTEN_ID && (
							<PdlPersonUtenIdentifikator
								formikBag={formikBag}
								path={`${path}.relatertPersonUtenFolkeregisteridentifikator`}
							/>
						)}

						{forelderBarnType === TypeAnsvarlig.NY && (
							<PdlNyPerson nyPersonPath={`${path}.nyRelatertPerson`} formikBag={formikBag} />
						)}

						<AvansertForm
							path={path}
							kanVelgeMaster={_.get(formikBag.values, `${path}.bekreftelsesdato`) === null}
						/>
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
