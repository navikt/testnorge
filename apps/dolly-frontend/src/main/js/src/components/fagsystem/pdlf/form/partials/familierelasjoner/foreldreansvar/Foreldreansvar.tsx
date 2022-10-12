import * as React from 'react'
import { FormikProps } from 'formik'
import {
	initialForeldreansvar,
	initialPdlBiPerson,
	initialPdlPerson,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { ForeldreBarnRelasjon, TypeAnsvarlig } from '~/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlNyPerson } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlPersonUtenIdentifikator } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { Alert } from '@navikt/ds-react'

interface ForeldreansvarForm {
	formikBag: FormikProps<{}>
}

type Target = {
	label: string
	value: string
}

export const Foreldreansvar = ({ formikBag }: ForeldreansvarForm) => {
	const ansvarlig = 'ansvarlig'
	const ansvarligUtenIdentifikator = 'ansvarligUtenIdentifikator'
	const nyAnsvarlig = 'nyAnsvarlig'
	const typeAnsvarlig = 'typeAnsvarlig'

	const handleChangeTypeAnsvarlig = (target: Target, path: string) => {
		const foreldreansvar = _get(formikBag.values, path)
		const foreldreansvarClone = _cloneDeep(foreldreansvar)

		_set(foreldreansvarClone, typeAnsvarlig, target?.value || null)
		if (!target) {
			_set(foreldreansvarClone, ansvarlig, undefined)
			_set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.EKSISTERENDE) {
			_set(foreldreansvarClone, ansvarlig, null)
			_set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.UTEN_ID) {
			_set(foreldreansvarClone, ansvarlig, undefined)
			_set(foreldreansvarClone, ansvarligUtenIdentifikator, initialPdlBiPerson)
			_set(foreldreansvarClone, nyAnsvarlig, undefined)
		}
		if (target?.value === TypeAnsvarlig.NY) {
			_set(foreldreansvarClone, ansvarlig, undefined)
			_set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_set(foreldreansvarClone, nyAnsvarlig, initialPdlPerson)
		}

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const handleChangeAnsvar = (target: Target, path: string) => {
		const foreldreansvar = _get(formikBag.values, path)
		const foreldreansvarClone = _cloneDeep(foreldreansvar)

		_set(foreldreansvarClone, 'ansvar', target?.value || null)
		if (target?.value !== 'ANDRE') {
			_set(foreldreansvarClone, typeAnsvarlig, undefined)
			_set(foreldreansvarClone, ansvarlig, undefined)
			_set(foreldreansvarClone, ansvarligUtenIdentifikator, undefined)
			_set(foreldreansvarClone, nyAnsvarlig, undefined)
		}

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const harBarn = () => {
		const relasjoner = _get(formikBag.values, 'pdldata.person.forelderBarnRelasjon')
		return relasjoner?.some(
			(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN'
		)
	}

	return (
		<>
			{!harBarn() && (
				<Alert variant={'warning'} style={{ marginBottom: '20px' }}>
					For å sette foreldreansvar må personen også ha et barn. Det kan du legge til ved å huke av
					for Har barn/foreldre under Familierelasjoner på forrige side, og sette en relasjon av
					typen barn.
				</Alert>
			)}
			<FormikDollyFieldArray
				name="pdldata.person.foreldreansvar"
				header={'Foreldreansvar'}
				newEntry={initialForeldreansvar}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
					const typeAnsvarlig = _get(formikBag.values, `${path}.typeAnsvarlig`)
					const ansvar = _get(formikBag.values, `${path}.ansvar`)

					return (
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name={`${path}.ansvar`}
								label="Hvem har ansvaret"
								options={Options('foreldreansvar')}
								onChange={(target: Target) => handleChangeAnsvar(target, path)}
							/>
							<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
							<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />

							{ansvar === 'ANDRE' && (
								<FormikSelect
									name={`${path}.typeAnsvarlig`}
									label="Type ansvarlig"
									options={Options('typeAnsvarlig')}
									onChange={(target: Target) => handleChangeTypeAnsvarlig(target, path)}
									size="medium"
								/>
							)}

							{typeAnsvarlig === TypeAnsvarlig.EKSISTERENDE && (
								<PdlEksisterendePerson
									eksisterendePersonPath={`${path}.ansvarlig`}
									label="Ansvarlig"
									formikBag={formikBag}
								/>
							)}

							{typeAnsvarlig === TypeAnsvarlig.UTEN_ID && (
								<PdlPersonUtenIdentifikator
									formikBag={formikBag}
									path={`${path}.ansvarligUtenIdentifikator`}
								/>
							)}

							{typeAnsvarlig === TypeAnsvarlig.NY && (
								<PdlNyPerson nyPersonPath={`${path}.nyAnsvarlig`} formikBag={formikBag} />
							)}

							<AvansertForm path={path} kanVelgeMaster={false} />
						</div>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
