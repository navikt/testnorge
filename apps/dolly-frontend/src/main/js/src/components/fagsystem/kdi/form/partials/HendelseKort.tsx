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
	startApen: boolean
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

const KortInnhold = styled.div`
	padding: 0 10px 10px;
	margin-top: 10px;
`

export const HendelseKort = ({
	header,
	status,
	beskrivelse,
	kanLukkes,
	startApen,
	handleRemove,
	children,
}: HendelseKortProps) => {
	const [apen, setApen] = useState(startApen)
	const innholdId = useId()
	const headingId = useId()
	const visInnhold = !kanLukkes || apen

	return (
		<Kort $status={status} aria-labelledby={headingId}>
			<KortHeader>
				<div style={{ flexGrow: 1 }}>
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
				</div>
				{handleRemove && <DeleteButton onClick={handleRemove} />}
				{kanLukkes && (
					<Button
						type="button"
						variant="tertiary"
						size="small"
						aria-expanded={apen}
						aria-controls={innholdId}
						icon={apen ? <ChevronUpIcon aria-hidden /> : <ChevronDownIcon aria-hidden />}
						onClick={() => setApen(!apen)}
					>
						<BodyShort as="span" size="small">
							{apen ? 'Skjul' : 'Vis'}
						</BodyShort>
					</Button>
				)}
			</KortHeader>
			{visInnhold && <KortInnhold id={innholdId}>{children}</KortInnhold>}
		</Kort>
	)
}
