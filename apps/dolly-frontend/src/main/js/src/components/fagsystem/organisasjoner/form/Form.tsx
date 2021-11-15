import React from 'react'
import * as Yup from 'yup'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { Detaljer } from './partials/Detaljer'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { adressePaths, kontaktPaths, organisasjonPaths } from './paths'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import { EnhetBestilling } from '~/components/fagsystem/organisasjoner/types'

type OrganisasjonFormProps = {
	formikBag: FormikProps<{ organisasjon: EnhetBestilling }>
}

const detaljerPaths = [organisasjonPaths, kontaktPaths, adressePaths].flat()

export const OrganisasjonForm = ({ formikBag }: OrganisasjonFormProps) => {
	return (
		<>
			{/* @ts-ignore */}
			<Vis attributt={detaljerPaths}>
				<Panel
					heading="Detaljer"
					hasErrors={panelError(formikBag, detaljerPaths)}
					iconType={'personinformasjon'}
					// @ts-ignore
					startOpen={() => erForste(formikBag.values, detaljerPaths)}
				>
					<Detaljer formikBag={formikBag} path="organisasjon" level={0} />
				</Panel>
			</Vis>
		</>
	)
}

const testSektorkode = (schema: any) => {
	return schema.test(
		'sektorkode',
		'Juridisk enhet må ha sektorkode hvis valgt',
		function harSektorkode(value: string) {
			if (value === undefined || value !== '') return true
			return this.createError({
				message: 'Feltet er påkrevd',
			})
		}
	)
}

const adresse = Yup.object({
	adresselinje: Yup.array().of(Yup.string()),
	postnr: Yup.string().nullable(),
	kommunenr: Yup.string().nullable(),
	landkode: requiredString,
	poststed: Yup.string(),
})

const organisasjon: any = Yup.object().shape({
	enhetstype: requiredString,
	naeringskode: ifPresent('$organisasjon.naeringskode', requiredString),
	sektorkode: ifPresent('$organisasjon.sektorkode', testSektorkode(Yup.string())),
	formaal: ifPresent('$organisasjon.formaal', requiredString),
	stiftelsesdato: ifPresent('$organisasjon.stiftelsesdato', requiredString),
	maalform: ifPresent('$organisasjon.maalform', requiredString),
	telefon: ifPresent('$organisasjon.telefon', requiredString),
	epost: ifPresent('$organisasjon.epost', requiredString),
	nettside: ifPresent('$organisasjon.nettside', requiredString),
	forretningsadresse: ifPresent('$organisasjon.forretningsadresse', adresse),
	postadresse: ifPresent('$organisasjon.postadresse', adresse),
	underenheter: ifPresent(
		'$organisasjon.underenheter',
		Yup.array()
			.transform((value) => Object.values(value))
			.of(Yup.lazy(() => organisasjon.default(undefined)))
	),
})

OrganisasjonForm.validation = {
	organisasjon: ifPresent('$organisasjon', organisasjon),
}
