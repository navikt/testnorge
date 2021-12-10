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
	const opts = useContext(BestillingsveilederContext)

	const navEnheter = SelectOptionsOppslag.hentNavEnheter()
	const navEnheterOptions = SelectOptionsOppslag.formatOptions('navEnheter', navEnheter)

	const paths = {
		rootPath: 'pdldata.person.sikkerhetstiltak',
		tpsMessagingRootPath: 'tpsMessaging.sikkerhetstiltak',
		tiltakstype: 'pdldata.person.sikkerhetstiltak.tiltakstype',
		beskrivelse: 'pdldata.person.sikkerhetstiltak.beskrivelse',
		kontaktperson: 'pdldata.person.sikkerhetstiltak.kontaktperson',
		gyldigFraOgMed: 'pdldata.person.sikkerhetstiltak.gyldigFraOgMed',
		gyldigTilOgMed: 'pdldata.person.sikkerhetstiltak.gyldigTilOgMed',
	}

	const indexBeskrSikkerhetTiltak = 7

	const handleSikkerhetstiltakChange = (option: Option) => {
		handleValueChange(option.value, 'tiltakstype')
		handleValueChange(
			option.label === 'Opphørt' ? option.label : option.label.substring(indexBeskrSikkerhetTiltak),
			'beskrivelse'
		)
	}

	const handleValueChange = (value: string, path: string) => {
		formikBag.setFieldValue(`${paths.rootPath}.${path}`, value)
		formikBag.setFieldValue(`${paths.tpsMessagingRootPath}.${path}`, value)
	}

	const [randomNavUsers, setRandomNavUsers] = useState([])
	const [personident, setPersonident] = useState(
		_get(formikBag.values, 'pdldata.person.sikkerhetstiltak.kontaktperson.personident')
	)

	useEffect(() => {
		setRandomNavUsers(genererTilfeldigeNavPersonidenter())
	}, [])

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<DollySelect
					name={paths.tiltakstype}
					label="Type sikkerhetstiltak"
					options={
						opts.personFoerLeggTil
							? Options('sikkerhetstiltakType')
							: Options('sikkerhetstiltakType').filter((option) => option.label !== 'Opphørt')
					}
					size="large"
					onChange={handleSikkerhetstiltakChange}
					value={_get(formikBag.values, paths.tiltakstype)}
					isClearable={false}
					feil={
						_get(formikBag.values, paths.tiltakstype) === '' && {
							feilmelding: 'Feltet er påkrevd',
						}
					}
				/>
				<FormikSelect
					value={personident}
					placeholder={personident ? personident : 'Velg..'}
					afterChange={(selected: { value: string; label: string }) => {
						setPersonident(selected.value)
					}}
					options={randomNavUsers}
					isClearable={false}
					name={`${paths.kontaktperson}.personident`}
					label={'Personident'}
				/>
				<FormikSelect
					name={`${paths.kontaktperson}.enhet`}
					label={'Enhetskode'}
					options={navEnheterOptions}
				/>
				<FormikDatepicker
					name={paths.gyldigFraOgMed}
					label="Sikkerhetstiltak starter"
					onChange={(date: Date) => {
						// @ts-ignore
						handleValueChange(date, 'gyldigFraOgMed')
					}}
				/>
				<FormikDatepicker
					name={paths.gyldigTilOgMed}
					label="Sikkerhetstiltak opphører"
					onChange={(date: Date) => {
						// @ts-ignore
						handleValueChange(date, 'gyldigTilOgMed')
					}}
				/>
			</div>
		</Vis>
	)
}
