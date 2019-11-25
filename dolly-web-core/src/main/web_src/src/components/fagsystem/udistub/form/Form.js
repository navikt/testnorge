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
	const { udistub } = formikBag.values
	return (
		<Vis attributt={attrPaths}>
			<Panel heading="UDI" hasErrors={panelError(formikBag)} startOpen>
				{udistub.oppholdStatus && <Oppholdsstatus formikBag={formikBag} />}
				{udistub.arbeidsadgang && <Arbeidsadgang formikBag={formikBag} />}
				{udistub.aliaser && <Alias formikBag={formikBag} />}
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
		aliaser: Yup.array()
			.of(
				Yup.object({
					nyIdent: Yup.boolean().required('Vennligst velg'),
					identtype: Yup.string().when('nyIdent', {
						is: true,
						then: Yup.string().required('Vennligst velg')
					})
				})
			)
			.nullable(),
		arbeidsadgang: Yup.object({
			arbeidsOmfang: Yup.string(),
			harArbeidsAdgang: Yup.string().required('Vennligst velg'),
			periode: {
				fra: Yup.date().required('Vennligst velg'),
				til: Yup.date().required('Vennligst velg')
			},
			typeArbeidsadgang: Yup.string()
		}).nullable(),
		flyktning: Yup.boolean()
			.required('Vennligst velg')
			.nullable(),
		oppholdStatus: Yup.object({}).nullable(),
		soeknadOmBeskyttelseUnderBehandling: Yup.string()
			.required('Vennligst velg')
			.nullable()
	})
}
