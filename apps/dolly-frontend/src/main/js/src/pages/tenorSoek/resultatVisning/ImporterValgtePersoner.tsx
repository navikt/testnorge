import { useLocation, useNavigate } from 'react-router'
import { usePdlPersonbolk } from '@/utils/hooks/usePdlPerson'
import { Button, Checkbox } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { EnterIcon } from '@navikt/aksel-icons'
import { top } from '@popperjs/core'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'

type ImporterValgtePersonerProps = {
	identer: Array<string>
	isMultiple: boolean
	inkluderPartnere: boolean
	setInkluderPartnere: any
}

const CheckboxWrapper = styled.div`
	display: flex;
	flex-wrap: wrap;
	align-items: baseline;
	height: 20px;
	margin-left: auto;
	margin-right: 15px;

	&& {
		.navds-checkbox {
			margin-bottom: 20px;
		}
	}
`

export const ImporterValgtePersoner = ({
	identer,
	isMultiple,
	inkluderPartnere,
	setInkluderPartnere,
}: ImporterValgtePersonerProps) => {
	const navigate = useNavigate()
	const location = useLocation()
	const { pdlPersoner, loading } = usePdlPersonbolk(identer)

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

	const handleSubmit = () => {
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

	return (
		<>
			{isMultiple ? (
				<>
					<CheckboxWrapper>
						<Checkbox
							checked={inkluderPartnere}
							size="small"
							onChange={(event) => setInkluderPartnere(event.target.checked)}
						>
							Inkluder eventuelle partnere
						</Checkbox>
						<Hjelpetekst placement={top}>
							Dersom en eller flere av de valgte personene har en partner, vil du inkludere
							partner(e) i importen?
						</Hjelpetekst>
					</CheckboxWrapper>
					<Button
						variant="primary"
						size="small"
						disabled={identer?.length < 1}
						loading={loading}
						onClick={handleSubmit}
					>
						{identer?.length === 1
							? 'Importer 1 valgt person'
							: `Importer ${identer?.length} valgte personer`}
					</Button>
				</>
			) : (
				<Button
					data-testid={TestComponentSelectors.BUTTON_IMPORTER_PERSONER}
					variant="tertiary"
					size="xsmall"
					icon={<EnterIcon />}
					loading={loading}
					onClick={handleSubmit}
					style={{ minWidth: '155px', height: '24px', marginTop: '10px' }}
				>
					Importer person
				</Button>
			)}
		</>
	)
}
