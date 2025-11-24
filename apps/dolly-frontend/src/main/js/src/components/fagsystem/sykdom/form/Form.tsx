import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import React, { useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { DetaljertSykemelding } from '@/components/fagsystem/sykdom/form/partials/DetaljertSykemelding'
import { useDollyFasteDataOrganisasjoner } from '@/utils/hooks/useDollyOrganisasjoner'
import { NySykemelding } from '@/components/fagsystem/sykdom/form/partials/NySykemelding'

export const sykemeldingAttributt = 'sykemelding.detaljertSykemelding'
export const nySykemeldingAttributt = 'sykemelding.nySykemelding'

const SykdomForm = () => {
	const formMethods = useFormContext()
	const detaljertSykemelding = formMethods.watch(sykemeldingAttributt)
	const nySykemelding = formMethods.watch(nySykemeldingAttributt)

	const { organisasjoner } = useDollyFasteDataOrganisasjoner(true)

	useEffect(() => {
		if (detaljertSykemelding?.mottaker?.orgNr && !detaljertSykemelding?.arbeidsgiver?.navn) {
			const valgtOrg = organisasjoner?.find(
				(org) => org.orgnummer === detaljertSykemelding.mottaker.orgNr,
			)
			formMethods.setValue(`${sykemeldingAttributt}.arbeidsgiver.navn`, valgtOrg?.navn)
			formMethods.setValue(`${sykemeldingAttributt}.mottaker`, {
				navn: valgtOrg?.navn || null,
				orgNr: valgtOrg?.orgnummer || null,
				adresse: {
					by: valgtOrg?.forretningsAdresse?.poststed || null,
					gate: valgtOrg?.forretningsAdresse?.adresselinje1 || null,
					land: valgtOrg?.forretningsAdresse?.landkode || null,
					postnummer: valgtOrg?.forretningsAdresse?.postnr || null,
				},
			})
			formMethods.trigger(sykemeldingAttributt)
		}
	}, [organisasjoner])

	return (
		// @ts-ignore
		<Vis attributt={[sykemeldingAttributt, nySykemeldingAttributt]}>
			<Panel
				heading="Sykemelding"
				hasErrors={panelError([sykemeldingAttributt, nySykemeldingAttributt])}
				iconType="sykdom"
				startOpen={erForsteEllerTest(formMethods.getValues(), [
					sykemeldingAttributt,
					nySykemeldingAttributt,
				])}
			>
				{detaljertSykemelding && <DetaljertSykemelding formMethods={formMethods} />}
				{nySykemelding && <NySykemelding />}
			</Panel>
		</Vis>
	)
}

SykdomForm.validation = validation

export default SykdomForm
