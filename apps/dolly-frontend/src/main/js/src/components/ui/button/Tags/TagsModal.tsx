import React, { useRef, useState } from 'react'
import { Button, Dialog, UNSAFE_Combobox } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useTags } from '@/utils/hooks/useTags'
import { LinkIcon } from '@navikt/aksel-icons'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { DollyApi } from '@/service/Api'

type Props = {
	gruppeId: number
	eksisterendeTags: string[]
}

export const TagsModal = ({ gruppeId, eksisterendeTags }: Props) => {
	const [selectedOptions, setSelectedOptions] = useState(eksisterendeTags)
	const [open, setOpen] = useState(false)
	const { tagOptions, loading } = useTags()
	const inputRef = useRef<HTMLInputElement>(null)
	const matchMutate = useMatchMutate()

	const [isLoading, setIsLoading] = useState(false)

	const handleSubmit = (event: any) => {
		event.preventDefault()
		setIsLoading(true)
		DollyApi.updateGruppeSendTags(gruppeId, selectedOptions)
			.then(() => matchMutate(REGEX_BACKEND_GRUPPER))
			.then(() => setOpen(false))
			.finally(() => setIsLoading(false))
	}

	const handleToggleSelected = (option: string, isSelected: boolean) => {
		if (isSelected) {
			setSelectedOptions([...selectedOptions, option])
		} else {
			setSelectedOptions(selectedOptions.filter((o) => o !== option))
		}
	}

	const handleOpenChange = (isOpen: boolean) => {
		if (!isLoading) {
			setOpen(isOpen)
		}
	}

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_TILKNYTT_TAGS}
				size="xsmall"
				variant="tertiary"
				icon={<LinkIcon aria-hidden />}
				onClick={() => setOpen(true)}
			>
				Tags
			</Button>
			<Dialog open={open} onOpenChange={handleOpenChange}>
				<Dialog.Popup initialFocusTo={inputRef}>
					<Dialog.Header>
						<Dialog.Title>Legg til tags på gruppe</Dialog.Title>
						<Dialog.Description>
							Tags gir deg mulighet til å identifisere dine PDL-personer på egen “tagged”
							Kafka-topic, der tags[dintag] legges til på responsen. Ta kontakt med team Dolly for
							ytterligere informasjon, eller for å legge til nye tags.
						</Dialog.Description>
					</Dialog.Header>
					<Dialog.Body>
						<form id="tilknytt-tags-form" onSubmit={handleSubmit}>
							<UNSAFE_Combobox
								label="Velg hvilke tags du ønsker å legge til"
								options={tagOptions}
								onToggleSelected={handleToggleSelected}
								selectedOptions={selectedOptions}
								isLoading={loading}
								ref={inputRef}
								placeholder={'Velg tags ...'}
								isMultiSelect
							/>
						</form>
					</Dialog.Body>
					<Dialog.Footer>
						<Dialog.CloseTrigger>
							<Button type="button" variant="secondary">
								Avbryt
							</Button>
						</Dialog.CloseTrigger>
						<Button form="tilknytt-tags-form" loading={isLoading}>
							Tilknytt tags
						</Button>
					</Dialog.Footer>
				</Dialog.Popup>
			</Dialog>
		</>
	)
}
