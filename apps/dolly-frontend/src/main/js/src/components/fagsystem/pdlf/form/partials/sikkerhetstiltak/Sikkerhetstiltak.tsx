import * as React from 'react'
import { useContext, useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { genererTilfeldigeNavPersonidenter } from '~/utils/GenererTilfeldigeNavPersonidenter'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { isToday } from 'date-fns'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { InputWarning } from '~/components/ui/form/inputWarning/inputWarning'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialSikkerhetstiltak,
	initialTpsSikkerhetstiltak,
} from '~/components/fagsystem/pdlf/form/initialValues'

interface SikkerhetstiltakValues {
	tiltakstype: string
	beskrivelse: string
	kontaktperson: Object
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
}

interface SikkerhetstiltakProps {
	formikBag: FormikProps<{ tpsf: SikkerhetstiltakValues }>
}

type Option = {
	value: string
	label: string
}

export const Sikkerhetstiltak = ({ formikBag }: SikkerhetstiltakProps) => {
	const sikkerhetstiltakListe = _get(formikBag.values, 'pdldata.person.sikkerhetstiltak')
	const sikkerhetstiltakListeTps = _get(formikBag.values, 'tpsMessaging.sikkerhetstiltak')

	if (!sikkerhetstiltakListe) return null

	const opts = useContext(BestillingsveilederContext)

	const navEnheter = SelectOptionsOppslag.hentNavEnheter()
	const navEnheterOptions = SelectOptionsOppslag.formatOptions('navEnheter', navEnheter)

	const paths = {
		rootPath: 'pdldata.person.sikkerhetstiltak',
		tpsMessagingRootPath: 'tpsMessaging.sikkerhetstiltak',
	}

	const indexBeskrSikkerhetTiltak = 7

	const handleSikkerhetstiltakChange = (option: Option, idx: number) => {
		handleValueChange(option.value, 'tiltakstype', idx)
		handleValueChange(
			option.label === 'Opphørt' ? option.label : option.label.substring(indexBeskrSikkerhetTiltak),
			'beskrivelse',
			idx
		)
	}

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.sikkerhetstiltak', [
			...sikkerhetstiltakListe,
			initialSikkerhetstiltak,
		])
		formikBag.setFieldValue('tpsMessaging.sikkerhetstiltak', [
			...sikkerhetstiltakListeTps,
			initialTpsSikkerhetstiltak,
		])
	}

	const handleValueChange = (value: string, name: string, idx: number) => {
		formikBag.setFieldValue(`${paths.rootPath}[${idx}].${name}`, value)
		formikBag.setFieldValue(`${paths.tpsMessagingRootPath}[${idx}].${name}`, value)
	}

	const handleRemoveEntry = (idx: number) => {
		sikkerhetstiltakListe.splice(idx, 1)
		sikkerhetstiltakListeTps.splice(idx, 1)
		formikBag.setFieldValue('pdldata.person.sikkerhetstiltak', sikkerhetstiltakListe)
		formikBag.setFieldValue('tpsMessaging.sikkerhetstiltak', sikkerhetstiltakListeTps)
	}

	const [randomNavUsers, setRandomNavUsers] = useState([])

	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter())
	}, [])

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<FormikDollyFieldArray
					name={'pdldata.person.sikkerhetstiltak'}
					header="Sikkerhetstiltak"
					newEntry={initialSikkerhetstiltak}
					canBeEmpty={false}
					handleNewEntry={handleNewEntry}
					handleRemoveEntry={handleRemoveEntry}
				>
					{(path: string, idx: number) => {
						const personident = _get(formikBag.values, `${path}.kontaktperson.personident`)
						return (
							<>
								<DollySelect
									name={`${path}.tiltakstype`}
									label="Type sikkerhetstiltak"
									options={
										opts.personFoerLeggTil
											? Options('sikkerhetstiltakType')
											: Options('sikkerhetstiltakType').filter(
													(option) => option.label !== 'Opphørt'
											  )
									}
									size="large"
									onChange={(option: Option) => handleSikkerhetstiltakChange(option, idx)}
									value={_get(formikBag.values, `${path}.tiltakstype`)}
									isClearable={false}
									feil={
										_get(formikBag.values, `${path}.tiltakstype`) === '' && {
											feilmelding: 'Feltet er påkrevd',
										}
									}
								/>
								<FormikSelect
									options={randomNavUsers}
									isClearable={false}
									name={`${path}.kontaktperson.personident`}
									placeholder={personident ? personident : 'Velg..'}
									label={'Kontaktperson'}
									fastfield={false}
								/>
								<FormikSelect
									name={`${path}.kontaktperson.enhet`}
									label={'NAV kontor'}
									size={'xxxlarge'}
									options={navEnheterOptions}
								/>
								<InputWarning
									visWarning={
										!isToday(
											_get(
												formikBag.values,
												`pdldata.person.sikkerhetstiltak[${idx}].gyldigFraOgMed`
											)
										)
									}
									warningText="TPS støtter kun sikkerhetstiltak fra gjeldende dato. Endre til dagens dato dersom et
							gyldig sikkerhetstiltak fra TPS er ønsket."
								>
									<FormikDatepicker
										name={`${path}.gyldigFraOgMed`}
										label="Sikkerhetstiltak starter"
										onChange={(date: Date) => {
											// @ts-ignore
											handleValueChange(date, 'gyldigFraOgMed', idx)
										}}
									/>
								</InputWarning>
								<FormikDatepicker
									name={`${path}.gyldigTilOgMed`}
									label="Sikkerhetstiltak opphører"
									onChange={(date: Date) => {
										// @ts-ignore
										handleValueChange(date, 'gyldigTilOgMed', idx)
									}}
								/>
								<AvansertForm path={path} kanVelgeMaster={false} />
							</>
						)
					}}
				</FormikDollyFieldArray>
			</div>
		</Vis>
	)
}
