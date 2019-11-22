import React from 'react'
import * as Yup from 'yup'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Alias } from './partials/Alias'
import { Annet } from './partials/Annet'

const attrPaths = [
	pathAttrs.kategori.opphold,
	pathAttrs.kategori.arbeidsadgang,
	pathAttrs.kategori.alias,
	pathAttrs.kategori.annet
].flat()

export const UdistubForm = ({ formikBag }) => {
	return (
		<Vis attributt={attrPaths}>
			<Panel heading="UDI" hasErrors={panelError(formikBag)} startOpen>
				<Oppholdsstatus formikBag={formikBag} />
				<Arbeidsadgang formikBag={formikBag} />
				<Alias formikBag={formikBag} />
				<Annet formikBag={formikBag} />
			</Panel>
		</Vis>
	)
}

UdistubForm.initialValues = attrs => {
	const initial = {
		udistub: {}
	}

	if (attrs.arbeidsadgang) {
		initial.udistub.arbeidsadgang = {
			arbeidsOmfang: '',
			harArbeidsAdgang: '',
			periode: {
				fra: '',
				til: ''
			},
			typeArbeidsadgang: ''
		}
	}

	if (attrs.oppholdStatus) {
		initial.udistub.oppholdStatus = {}
	}

	if (attrs.aliaser) {
		initial.udistub.aliaser = [
			{
				identtype: '',
				nyIdent: ''
			}
		]
	}

	if (attrs.flyktning) {
		initial.udistub.flyktning = ''
	}

	if (attrs.asylsoker) {
		initial.udistub.soeknadOmBeskyttelseUnderBehandling = ''
	}

	return initial
}

UdistubForm.validation = {
	udistub: Yup.object({
		aliaser: Yup.array().of(
			Yup.object({
				identtype: Yup.string().when('nyIdent', {
					is: true,
					then: Yup.string().required('Vennligst velg')
				}),
				nyIdent: Yup.boolean().required('Vennligst velg')
			})
		),
		arbeidsadgang: Yup.object({
			arbeidsOmfang: Yup.string(),
			harArbeidsAdgang: Yup.string().required('Vennligst velg'),
			periode: {
				fra: Yup.string().typeError('Formatet må være DD.MM.YYYY'),
				til: Yup.string().typeError('Formatet må være DD.MM.YYYY')
			},
			typeArbeidsadgang: Yup.string()
		}),
		flyktning: Yup.boolean().required('Vennligst velg'),
		oppholdStatus: Yup.object({}), //TODO fiks denne!
		soeknadOmBeskyttelseUnderBehandling: Yup.string().required('Vennligst velg')
	})
}

// required:
// --- OPPHOLDSSTATUS
// 	oppholdsstatus
// 	eøs eller efta type opphold
// 	eøs eller efta beslutning om oppholdsrett (grunnlag) (x3 (???) pga av ulike dropdowns)
// 	tredjelands borgere valg
// 	type oppholdstillatelse
