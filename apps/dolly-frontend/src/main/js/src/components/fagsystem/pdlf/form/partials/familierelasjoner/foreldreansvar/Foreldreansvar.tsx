import * as React from 'react'
import { FormikProps } from 'formik'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
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
import { AdresseKodeverk } from '~/config/kodeverk'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'

interface ForeldreansvarForm {
	formikBag: FormikProps<{}>
	identOptions: Array<Option>
}

export const Foreldreansvar = ({ formikBag, identOptions }: ForeldreansvarForm) => {
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const fornavnOptions = SelectOptionsOppslag.formatOptions('fornavn', navnInfo)
	const mellomnavnOptions = SelectOptionsOppslag.formatOptions('mellomnavn', navnInfo)
	const etternavnOptions = SelectOptionsOppslag.formatOptions('etternavn', navnInfo)

	const handleChangeTypeAnsvarlig = (target, path) => {
		const foreldreansvar = _get(formikBag.values, path)
		const foreldreansvarClone = _cloneDeep(foreldreansvar)

		_set(foreldreansvarClone, 'typeAnsvarlig', target?.value || null)
		if (!target) {
			_set(foreldreansvarClone, 'ansvarlig', undefined)
			_set(foreldreansvarClone, 'ansvarligUtenIdentifikator', undefined)
			_set(foreldreansvarClone, 'nyAnsvarlig', undefined)
		}
		if (target?.value === 'EKSISTERENDE') {
			_set(foreldreansvarClone, 'ansvarlig', null)
			_set(foreldreansvarClone, 'ansvarligUtenIdentifikator', undefined)
			_set(foreldreansvarClone, 'nyAnsvarlig', undefined)
		}
		if (target?.value === 'UTEN_ID') {
			_set(foreldreansvarClone, 'ansvarlig', undefined)
			_set(foreldreansvarClone, 'ansvarligUtenIdentifikator', initialPdlBiPerson)
			_set(foreldreansvarClone, 'nyAnsvarlig', undefined)
		}
		if (target?.value === 'NY') {
			_set(foreldreansvarClone, 'ansvarlig', undefined)
			_set(foreldreansvarClone, 'ansvarligUtenIdentifikator', undefined)
			_set(foreldreansvarClone, 'nyAnsvarlig', initialPdlPerson)
		}

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const handleChangeAnsvar = (target, path) => {
		const foreldreansvar = _get(formikBag.values, path)
		const foreldreansvarClone = _cloneDeep(foreldreansvar)

		_set(foreldreansvarClone, 'ansvar', target?.value || null)
		if (target?.value !== 'ANDRE') {
			_set(foreldreansvarClone, 'typeAnsvarlig', undefined)
			_set(foreldreansvarClone, 'ansvarlig', undefined)
			_set(foreldreansvarClone, 'ansvarligUtenIdentifikator', undefined)
			_set(foreldreansvarClone, 'nyAnsvarlig', undefined)
		}

		formikBag.setFieldValue(path, foreldreansvarClone)
	}

	const harBarn = () => {
		const relasjoner = _get(formikBag.values, 'pdldata.person.forelderBarnRelasjon')
		return relasjoner?.some((relasjon) => relasjon.relatertPersonsRolle === 'BARN')
	}

	// Feil: error:Bad Request: message:Foreldreansvar: barn mangler / barnets foreldrerelasjon til mor ikke funnet:

	return (
		<>
			{!harBarn() && (
				<AlertStripeAdvarsel style={{ marginBottom: '20px' }}>
					For å sette foreldreansvar må personen også ha et barn. Det kan du legge til ved å huke av
					for Har barn/foreldre under Familierelasjoner på forrige side, og sette en relasjon av
					typen barn.
				</AlertStripeAdvarsel>
			)}
			<FormikDollyFieldArray
				name="pdldata.person.foreldreansvar"
				header={'Foreldreansvar'}
				newEntry={initialForeldreansvar}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => {
					const typeAnsvarlig = _get(formikBag.values, `${path}.typeAnsvarlig`)
					const ansvar = _get(formikBag.values, `${path}.ansvar`)

					return (
						<div className="flexbox--flex-wrap">
							<FormikSelect
								name={`${path}.ansvar`}
								label="Hvem har ansvaret"
								options={Options('foreldreansvar')}
								onChange={(target) => handleChangeAnsvar(target, path)}
							/>
							<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
							<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />

							{ansvar === 'ANDRE' && (
								<FormikSelect
									name={`${path}.typeAnsvarlig`}
									label="Type ansvarlig"
									options={Options('typeAnsvarlig')}
									onChange={(target) => handleChangeTypeAnsvarlig(target, path)}
									size="medium"
								/>
							)}

							{typeAnsvarlig === 'EKSISTERENDE' && (
								<FormikSelect
									name={`${path}.ansvarlig`}
									label="Ansvarlig"
									options={identOptions}
									size="xlarge"
								/>
							)}

							{typeAnsvarlig === 'UTEN_ID' && (
								<>
									<FormikSelect
										name={`${path}.ansvarligUtenIdentifikator.kjoenn`}
										label="Kjønn"
										options={Options('kjoenn')}
									/>
									<FormikDatepicker
										name={`${path}.ansvarligUtenIdentifikator.foedselsdato`}
										label="Fødselsdato"
									/>
									<FormikSelect
										name={`${path}.ansvarligUtenIdentifikator.statsborgerskap`}
										label="Statsborgerskap"
										kodeverk={AdresseKodeverk.StatsborgerskapLand}
										size="large"
									/>
									<FormikSelect
										name={`${path}.ansvarligUtenIdentifikator.navn.fornavn`}
										label="Fornavn"
										options={fornavnOptions}
									/>
									<FormikSelect
										name={`${path}.ansvarligUtenIdentifikator.navn.mellomnavn`}
										label="Mellomnavn"
										options={mellomnavnOptions}
									/>
									<FormikSelect
										name={`${path}.ansvarligUtenIdentifikator.navn.etternavn`}
										label="Etternavn"
										options={etternavnOptions}
									/>
								</>
							)}

							{typeAnsvarlig === 'NY' && (
								<PdlPersonForm path={`${path}.nyAnsvarlig`} formikBag={formikBag} />
							)}

							<AvansertForm path={path} kanVelgeMaster={false} />
						</div>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
