import React, { useRef, useState } from 'react'
import { actions } from '@/ducks/gruppe'
import { Button, Dialog, UNSAFE_Combobox } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useDispatch } from 'react-redux'
import { useTags } from '@/utils/hooks/useTags'
import { LinkIcon } from '@navikt/aksel-icons'

type Props = {
	gruppeId: number
	eksisterendeTags: string[]
	isSending: boolean
}

export const TagsModal = ({ gruppeId, eksisterendeTags, isSending }: Props) => {
	const dispatch = useDispatch()
	const [selectedOptions, setSelectedOptions] = useState(eksisterendeTags)
	const { tagOptions, loading } = useTags()
	const inputRef = useRef<HTMLInputElement>(null)

	const handleSubmit = () => {
		dispatch(actions.sendGruppeTags(gruppeId, selectedOptions))
	}

	const handleToggleSelected = (option: string, isSelected: boolean) => {
		if (isSelected) {
			setSelectedOptions([...selectedOptions, option])
		} else {
			setSelectedOptions(selectedOptions.filter((o) => o !== option))
		}
	}

	return (
		<Dialog>
			<Dialog.Trigger>
				<Button
					data-testid={TestComponentSelectors.BUTTON_TILKNYTT_TAGS}
					size="xsmall"
					variant="tertiary"
					icon={<LinkIcon aria-hidden />}
					loading={isSending}
				>
					Tags
				</Button>
			</Dialog.Trigger>
			<Dialog.Popup initialFocusTo={inputRef}>
				<Dialog.Header>
					<Dialog.Title>Legg til tags på gruppe</Dialog.Title>
					<Dialog.Description>
						Tags gir deg mulighet til å identifisere dine PDL-personer på egen “tagged” Kafka-topic,
						der tags[dintag] legges til på responsen. Ta kontakt med team Dolly for ytterligere
						informasjon, eller for å legge til nye tags.
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
					<Button form="tilknytt-tags-form">Tilknytt tags</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
