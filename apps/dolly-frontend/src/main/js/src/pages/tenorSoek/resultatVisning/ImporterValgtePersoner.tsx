import { useLocation, useNavigate } from 'react-router'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { EnterIcon } from '@navikt/aksel-icons'
import { TestComponentSelectors } from '#/mocks/Selectors'

type ImporterValgtePersonerProps = {
	identer: Array<string>
	isMultiple: boolean
}

const ImporterPartnerDialog = ({ open, setOpen, handleSubmit }) => (
	<Dialog open={open} onOpenChange={setOpen}>
		<Dialog.Popup>
			<Dialog.Header>
				<Dialog.Title>Inkluder partner</Dialog.Title>
			</Dialog.Header>
			<Dialog.Body>
				<BodyLong>
					Én eller flere av personene du importerer har en partner. Ønsker du å importere partner(e)
					også?
				</BodyLong>
			</Dialog.Body>
			<Dialog.Footer>
				<Dialog.CloseTrigger>
					<Button variant="secondary" onClick={() => handleSubmit(false)}>
						Nei, importer kun valgte personer
					</Button>
				</Dialog.CloseTrigger>
				<Dialog.CloseTrigger>
					<Button type="button" onClick={() => handleSubmit(true)}>
						Ja, inkluder partner
					</Button>
				</Dialog.CloseTrigger>
			</Dialog.Footer>
		</Dialog.Popup>
	</Dialog>
)

export const ImporterValgtePersoner = ({ identer, isMultiple }: ImporterValgtePersonerProps) => {
	const navigate = useNavigate()
	const location = useLocation()
	const { pdlPersoner, loading } = usePdlPersonbolk(identer)

	const [open, setOpen] = useState(false)
	const [partnere, setPartnere] = useState([]) as any

	const partnerSivilstander = ['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER']

	useEffect(() => {
		const partnerListe: Array<any> = []
		pdlPersoner?.hentPersonBolk?.map((ident) => {
			const partner = ident.person?.sivilstand?.filter(
				(sivilstand) =>
					!sivilstand?.metadata?.historisk && partnerSivilstander.includes(sivilstand?.type),
			)?.[0]?.relatertVedSivilstand
			if (partner) {
				partnerListe.push(partner)
			}
		})
		setPartnere(partnerListe)
	}, [pdlPersoner])

	const handleSubmit = (inkluderPartnere: boolean) => {
		const valgtePartnere = inkluderPartnere ? partnere : []

		navigate('/importer', {
			state: {
				importPersoner: identer.concat(valgtePartnere).map((ident) => ({
					ident,
					data: {
						hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)?.person,
						hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === ident)?.identer,
					},
				})),
				gruppe: location?.state?.gruppe,
				identMaster: 'PDL',
			},
		})
	}

	const handleImport = () => {
		if (partnere?.length > 0) {
			setOpen(true)
		} else handleSubmit(false)
	}

	return (
		<>
			<ImporterPartnerDialog open={open} setOpen={setOpen} handleSubmit={handleSubmit} />
			{isMultiple ? (
				<Button
					variant="primary"
					size="small"
					disabled={identer?.length < 1}
					loading={loading}
					onClick={handleImport}
				>
					{identer?.length === 1
						? 'Importer 1 valgt person'
						: `Importer ${identer?.length} valgte personer`}
				</Button>
			) : (
				<Button
					data-testid={TestComponentSelectors.BUTTON_IMPORTER_PERSONER}
					variant="tertiary"
					size="xsmall"
					icon={<EnterIcon aria-hidden />}
					loading={loading}
					onClick={handleImport}
					style={{ minWidth: '155px', height: '24px', marginTop: '10px' }}
				>
					Importer person
				</Button>
			)}
		</>
	)
}
