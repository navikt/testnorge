import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import React, { useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { DetaljertSykemelding } from '@/components/fagsystem/sykdom/form/partials/DetaljertSykemelding'
import { useDollyFasteDataOrganisasjoner } from '@/utils/hooks/useOrganisasjoner'

export const sykdomAttributt = 'sykemelding'

const SykdomForm = () => {
	const formMethods = useFormContext()
	const detaljertSykemelding = formMethods.watch(`${sykdomAttributt}.detaljertSykemelding`)

	const { organisasjoner } = useDollyFasteDataOrganisasjoner(true)

	useEffect(() => {
		if (detaljertSykemelding?.mottaker?.orgNr && !detaljertSykemelding?.arbeidsgiver?.navn) {
			const valgtOrg = organisasjoner?.find(
				(org) => org.orgnummer === detaljertSykemelding.mottaker.orgNr,
			)
			formMethods.setValue(
				`${sykdomAttributt}.detaljertSykemelding.arbeidsgiver.navn`,
				valgtOrg?.navn,
			)
			formMethods.setValue(`${sykdomAttributt}.detaljertSykemelding.mottaker`, {
				navn: valgtOrg?.navn || null,
				orgNr: valgtOrg?.orgnummer || null,
				adresse: {
					by: valgtOrg?.forretningsAdresse?.poststed || null,
					gate: valgtOrg?.forretningsAdresse?.adresselinje1 || null,
					land: valgtOrg?.forretningsAdresse?.landkode || null,
					postnummer: valgtOrg?.forretningsAdresse?.postnr || null,
				},
			})
			formMethods.trigger(`${sykdomAttributt}.detaljertSykemelding`)
		}
	}, [organisasjoner])

	return (
		// @ts-ignore
		<Vis attributt={sykdomAttributt}>
			<Panel
				heading="Sykemelding"
				hasErrors={panelError(sykdomAttributt)}
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [sykdomAttributt])}
			>
				<DetaljertSykemelding formMethods={formMethods} />
			</Panel>
		</Vis>
	)
}

SykdomForm.validation = validation

export default SykdomForm
