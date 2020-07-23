import React from 'react'
import { render } from 'enzyme'
import FavoriteButton from '../FavoriteButton/FavoriteButton'

describe('FavoriteButton.js', () => {
	const add = jest.fn()
	const remove = jest.fn()
	it('should render a favoritebutton where isFavorite=true', () => {
		const wrapper = render(
			<FavoriteButton addFavorite={add} removeFavorite={remove} isFavorite={true} />
		)
		expect(wrapper).toHaveLength(1)
	})

	it('should render a favoritebutton where isFavorite=true', () => {
		const wrapper = render(
			<FavoriteButton addFavorite={add} removeFavorite={remove} isFavorite={false} />
		)
		expect(wrapper).toHaveLength(1)
	})
})
