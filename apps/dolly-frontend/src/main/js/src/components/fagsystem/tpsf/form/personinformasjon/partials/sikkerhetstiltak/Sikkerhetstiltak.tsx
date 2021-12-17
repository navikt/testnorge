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
		rootPath: 'pdldata.person.sikkerhetstiltak[0]',
		tiltakstype: 'pdldata.person.sikkerhetstiltak[0].tiltakstype',
		kontaktperson: 'pdldata.person.sikkerhetstiltak[0].kontaktperson',
		gyldigFraOgMed: 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed',
		gyldigTilOgMed: 'pdldata.person.sikkerhetstiltak[0].gyldigTilOgMed',
		tpsMessagingRootPath: 'tpsMessaging.sikkerhetstiltak',
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
		_get(formikBag.values, 'pdldata.person.sikkerhetstiltak[0].kontaktperson.personident')
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
					label={'Kontaktperson'}
				/>
				<FormikSelect
					name={`${paths.kontaktperson}.enhet`}
					label={'NAV kontor'}
					size={'xxxlarge'}
					options={navEnheterOptions}
				/>
				<InputWarning
					visWarning={
						!isToday(_get(formikBag.values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
					}
					warningText="TPS støtter kun sikkerhetstiltak fra gjeldende dato. Endre til dagens dato dersom et
						gyldig sikkerhetstiltak fra TPS er ønsket."
				>
					<FormikDatepicker
						name={paths.gyldigFraOgMed}
						label="Sikkerhetstiltak starter"
						onChange={(date: Date) => {
							// @ts-ignore
							handleValueChange(date, 'gyldigFraOgMed')
						}}
					/>
				</InputWarning>
				<FormikDatepicker
					name={paths.gyldigTilOgMed}
					label="Sikkerhetstiltak opphører"
					onChange={(date: Date) => {
						// @ts-ignore
						handleValueChange(date, 'gyldigTilOgMed')
					}}
				/>
				<AvansertForm path={paths.rootPath} kanVelgeMaster={false} />
			</div>
		</Vis>
	)
}
