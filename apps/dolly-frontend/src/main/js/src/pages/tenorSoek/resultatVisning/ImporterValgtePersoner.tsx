import { useLocation, useNavigate } from 'react-router'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { Button, Checkbox } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { MalValg } from '@/pages/tenorSoek/resultatVisning/MalValg'
import { EnterIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { top } from '@popperjs/core'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'

type ImporterValgtePersonerProps = {
	identer: Array<string>
	isMultiple: boolean
}

const CheckboxWrapper = styled.div`
	display: flex;
	flex-wrap: wrap;
	align-items: baseline;
	margin-top: 20px;
	border-bottom: 1px solid #ccc;

	&& {
		.navds-checkbox {
			margin-bottom: 20px;
		}
	}
`

export const ImporterValgtePersoner = ({ identer, isMultiple }: ImporterValgtePersonerProps) => {
	const navigate = useNavigate()
	const location = useLocation()
	const { pdlPersoner, loading } = usePdlPersonbolk(identer)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [valgtMal, setValgtMal] = useState(null)

	const [partnere, setPartnere] = useState([])
	const [valgtePartnere, setValgtePartnere] = useState([])

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
		// @ts-ignore
		setPartnere(partnerListe)
	}, [pdlPersoner])

	const handleClick = () => {
		navigate(`/importer`, {
			state: {
				importPersoner: identer.concat(valgtePartnere).map((ident) => {
					return {
						ident: ident,
						data: {
							hentPerson: pdlPersoner?.hentPersonBolk?.find((p) => p.ident === ident)?.person,
							hentIdenter: pdlPersoner?.hentIdenterBolk?.find((p) => p.ident === ident)?.identer,
						},
					}
				}),
				mal: valgtMal,
				gruppe: location?.state?.gruppe,
				identMaster: 'PDL',
			},
		})
	}

	const handleClickPartnere = (event: any) => {
		if (event.target.checked) {
			setValgtePartnere(partnere)
		} else {
			setValgtePartnere([])
		}
	}

	return (
		<>
			{isMultiple ? (
				<Button
					variant="primary"
					size="small"
					disabled={identer?.length < 1}
					loading={loading}
					onClick={openModal}
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
					icon={<EnterIcon />}
					loading={loading}
					onClick={openModal}
					style={{ minWidth: '155px', height: '24px', marginTop: '10px' }}
				>
					Importer person
				</Button>
			)}
			{/*// @ts-ignore*/}
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%" overflow="auto">
				<div>
					<h1>{identer?.length === 1 ? 'Importer person' : 'Importer personer'}</h1>
					{partnere.length > 0 && (
						<CheckboxWrapper>
							<Checkbox
								size="small"
								onChange={(event) => handleClickPartnere(event)}
								style={{ marginBottom: '20px' }}
							>
								Inkluder partner
							</Checkbox>
							<Hjelpetekst placement={top}>
								En eller flere av de valgte personene har en partner. <br /> Vil du inkludere
								partner(e) i importen?
							</Hjelpetekst>
						</CheckboxWrapper>
					)}
					<MalValg setValgtMal={setValgtMal} />
					<div className="dollymodal_buttons dollymodal_buttons--center">
						<Button
							data-testid={TestComponentSelectors.BUTTON_IMPORTER}
							onClick={() => handleClick()}
						>
							Importer
						</Button>
						<Button variant="secondary" onClick={closeModal}>
							Avbryt
						</Button>
					</div>
				</div>
			</DollyModal>
		</>
	)
}
