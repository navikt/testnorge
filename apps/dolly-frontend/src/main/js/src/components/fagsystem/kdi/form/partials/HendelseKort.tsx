import React, { ReactNode, useId, useState } from 'react'
import styled from 'styled-components'
import { BodyShort, Button, Heading, HStack, Tag } from '@navikt/ds-react'
import { ChevronDownIcon, ChevronUpIcon } from '@navikt/aksel-icons'
import { DeleteButton } from '@/components/ui/form/fieldArray/DollyFieldArray'

export type HendelseStatus = {
	erNy: boolean
	harRedigering: boolean
	erAnnullert: boolean
}

type HendelseKortProps = {
	header: string
	status: HendelseStatus
	beskrivelse?: string
	kanLukkes: boolean
	startAapen: boolean
	handleRemove?: () => void
	children: ReactNode
}

const kantfarge = ({ erNy, harRedigering, erAnnullert }: HendelseStatus) => {
	if (harRedigering) {
		return 'var(--ax-border-meta-purple)'
	}
	if (erAnnullert) {
		return 'var(--ax-border-danger)'
	}
	if (erNy) {
		return 'var(--ax-border-accent)'
	}
	return 'var(--ax-border-neutral-subtle)'
}

const Kort = styled.section<{ $status: HendelseStatus }>`
	width: 100%;
	margin-bottom: 10px;
	border: 1px solid var(--ax-border-neutral-subtle);
	border-left: 4px solid ${({ $status }) => kantfarge($status)};
	border-radius: 4px;
	background-color: ${({ $status }) =>
		$status.harRedigering ? 'var(--ax-bg-meta-purple-soft)' : 'var(--ax-bg-default)'};

	.aksel-heading {
		font-size: revert-layer;
		line-height: revert-layer;
		margin: revert-layer;
	}
`

const KortHeader = styled.div`
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 8px 10px;
`

const KortTittel = styled.div`
	flex-grow: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
	min-height: 2rem;
`

const KortInnhold = styled.div<{ $kanLukkes: boolean }>`
	padding: 0 10px 10px;
	margin-top: ${({ $kanLukkes }) => ($kanLukkes ? '10px' : '0')};
`

export const HendelseKort = ({
	header,
	status,
	beskrivelse,
	kanLukkes,
	startAapen,
	handleRemove,
	children,
}: HendelseKortProps) => {
	const [aapen, setAapen] = useState(startAapen)
	const innholdId = useId()
	const headingId = useId()
	const visInnhold = !kanLukkes || aapen

	return (
		<Kort $status={status} aria-labelledby={headingId}>
			<KortHeader>
				<KortTittel>
					<HStack gap="space-8" align="center">
						<Heading size="xsmall" level="3" id={headingId}>
							{header}
						</Heading>
						{status.erNy && (
							<Tag variant="outline" size="xsmall" data-color="info">
								Ny
							</Tag>
						)}
						{status.harRedigering && (
							<Tag variant="outline" size="xsmall" data-color="meta-purple">
								Redigert
							</Tag>
						)}
						{status.erAnnullert && (
							<Tag variant="outline" size="xsmall" data-color="danger">
								Annullert
							</Tag>
						)}
					</HStack>
					{beskrivelse && (
						<BodyShort size="small" textColor="subtle">
							{beskrivelse}
						</BodyShort>
					)}
				</KortTittel>
				{handleRemove && <DeleteButton onClick={handleRemove} />}
				{kanLukkes && (
					<Button
						type="button"
						variant="tertiary"
						size="small"
						aria-expanded={aapen}
						aria-controls={innholdId}
						icon={aapen ? <ChevronUpIcon aria-hidden /> : <ChevronDownIcon aria-hidden />}
						onClick={() => setAapen(!aapen)}
					>
						<BodyShort as="span" size="small">
							{aapen ? 'Skjul' : 'Vis'}
						</BodyShort>
					</Button>
				)}
			</KortHeader>
			{visInnhold && (
				<KortInnhold id={innholdId} $kanLukkes={kanLukkes}>
					{children}
				</KortInnhold>
			)}
		</Kort>
	)
}
